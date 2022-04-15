package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.*
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.extended.awaitBool
import com.kairlec.koj.dao.extended.awaitOrNull
import com.kairlec.koj.dao.extended.list
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.ProblemConfig
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import com.kairlec.koj.dao.with
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(
    private val dslAccess: DSLAccess,
) {

    suspend fun getProblems(
        tags: List<String> = emptyList(),
        listCondition: ListCondition
    ): PageData<SimpleProblem> {
        return dslAccess.with {
            val data = select(PROBLEM.ID, PROBLEM.NAME, PROBLEM.SPJ, DSL.field("tr.tag_names"))
                .from(PROBLEM)
                .innerJoin(
                    select(
                        TAG_BELONG_PROBLEM.PROBLEM_ID,
                        DSL.groupConcat(PROBLEM_TAG.NAME).separator(",").`as`("tag_names")
                    )
                        .from(PROBLEM_TAG)
                        .join(TAG_BELONG_PROBLEM)
                        .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                        .let {
                            if (tags.isEmpty()) {
                                it
                            } else {
                                it.where(PROBLEM_TAG.NAME.`in`(tags))
                            }
                        }
                        .asTable("tr")
                )
                .on(TAG_BELONG_PROBLEM.PROBLEM_ID.eq(PROBLEM.ID))
                .list(PROBLEM, listCondition)
                .asFlow().map {
                    SimpleProblem(
                        id = it[PROBLEM.ID],
                        name = it[PROBLEM.NAME],
                        spj = it[PROBLEM.SPJ],
                        idx = null,
                        tags = it["tag_names"].toString().split(",")
                    )
                }
            val count = selectCount()
                .from(PROBLEM)
                .list(listCondition)
                .awaitOrNull(0)
            data pg count
        }
    }

    fun getProblems(
        competitionId: Long,
    ): Flow<SimpleProblem> {
        return dslAccess.flux {
            select(PROBLEM.ID, PROBLEM.NAME, PROBLEM.SPJ, PROBLEM_BELONG_COMPETITION.IDX, DSL.field("tr.tag_names"))
                .from(PROBLEM)
                .leftJoin(PROBLEM_BELONG_COMPETITION)
                .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
                .innerJoin(
                    select(
                        TAG_BELONG_PROBLEM.PROBLEM_ID,
                        DSL.groupConcat(PROBLEM_TAG.NAME).separator(",").`as`("tag_names")
                    )
                        .from(PROBLEM_TAG)
                        .join(TAG_BELONG_PROBLEM)
                        .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                        .asTable("tr")
                )
                .on(TAG_BELONG_PROBLEM.PROBLEM_ID.eq(PROBLEM.ID))
                .where(PROBLEM_BELONG_COMPETITION.COMPETITION_ID.eq(competitionId))
                .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
        }.asFlow().map {
            SimpleProblem(
                id = it[PROBLEM.ID],
                name = it[PROBLEM.NAME],
                spj = it[PROBLEM.SPJ],
                idx = it[PROBLEM_BELONG_COMPETITION.IDX],
                tags = it["tag_names"].toString().split(",")
            )
        }
    }

    suspend fun getProblem(
        id: Long
    ): Problem? {
        return dslAccess.with {
            val record = select(*PROBLEM.fields(), *PROBLEM_BELONG_COMPETITION.fields(), DSL.field("tr.tag_names"))
                .from(PROBLEM)
                .leftJoin(PROBLEM_BELONG_COMPETITION)
                .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
                .innerJoin(
                    select(
                        TAG_BELONG_PROBLEM.PROBLEM_ID,
                        DSL.groupConcat(PROBLEM_TAG.NAME).separator(",").`as`("tag_names")
                    )
                        .from(PROBLEM_TAG)
                        .join(TAG_BELONG_PROBLEM)
                        .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                        .asTable("tr")
                )
                .on(TAG_BELONG_PROBLEM.PROBLEM_ID.eq(PROBLEM.ID))
                .where(PROBLEM.ID.eq(id))
                .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
                .awaitFirstOrNull() ?: return@with null
            Problem(
                id = record[PROBLEM.ID],
                name = record[PROBLEM.NAME],
                content = record[PROBLEM.CONTENT],
                spj = record[PROBLEM.SPJ],
                createTime = record[PROBLEM.CREATE_TIME],
                updateTime = record[PROBLEM.UPDATE_TIME],
                config = selectFrom(PROBLEM_CONFIG)
                    .where(PROBLEM_CONFIG.PROBLEM_ID.eq(id))
                    .asFlow().map {
                        ProblemConfig(
                            languageId = it[PROBLEM_CONFIG.LANGUAGE_ID],
                            memoryLimit = it[PROBLEM_CONFIG.MEMORY],
                            timeLimit = it[PROBLEM_CONFIG.TIME],
                            createTime = it[PROBLEM_CONFIG.CREATE_TIME],
                            updateTime = it[PROBLEM_CONFIG.UPDATE_TIME]
                        )
                    }.toList(),
                idx = record[PROBLEM_BELONG_COMPETITION.IDX],
                tags = record["tag_names"].toString().split(",")
            )
        }
    }

    suspend fun getTags(listCondition: ListCondition):PageData<ProblemTagRecord>{
        return dslAccess.with {
            val data = selectFrom(PROBLEM_TAG)
                .list(PROBLEM_TAG, listCondition)
                .asFlow()
            val count = selectCount()
                .from(PROBLEM_TAG)
                .list(listCondition)
                .awaitOrNull(0)
            data pg count
        }
    }

    suspend fun newProblem(
        name: String,
        content: String,
        spj: Boolean,
    ): Long? {
        return dslAccess.with {
            insertInto(PROBLEM, PROBLEM.NAME, PROBLEM.CONTENT, PROBLEM.SPJ)
                .values(name, content, spj)
                .returningResult(PROBLEM.ID)
                .awaitOrNull()
        }
    }

    suspend fun updateProblem(
        id: Long,
        name: String?,
        content: String?,
        spj: Boolean?,
    ): Boolean {
        if (name == null && content == null && spj == null) return false
        return dslAccess.with {
            update(PROBLEM)
                .setIfNotNull(PROBLEM.NAME, name)
                .setIfNotNull(PROBLEM.CONTENT, content)
                .setIfNotNull(PROBLEM.SPJ, spj)
                .where(PROBLEM.ID.eq(id))
                .awaitBool()
        }
    }

    suspend fun updateTag(
        tagId: Long,
        name: String
    ): Boolean {
        return dslAccess.with {
            update(PROBLEM_TAG)
                .set(PROBLEM_TAG.NAME, name)
                .where(PROBLEM_TAG.ID.eq(tagId))
                .awaitBool()
        }
    }

    suspend fun removeProblem(
        id: Long
    ): Boolean {
        return dslAccess.with {
            deleteFrom(PROBLEM)
                .where(PROBLEM.ID.eq(id))
                .awaitBool()
        }
    }


    suspend fun newTag(
        name: String
    ): Long? {
        return dslAccess.with {
            insertInto(PROBLEM_TAG, PROBLEM_TAG.NAME)
                .values(name)
                .returningResult(PROBLEM_TAG.ID)
                .awaitOrNull()
        }
    }

    suspend fun removeTag(
        id: Long
    ): Boolean {
        return dslAccess.with {
            deleteFrom(PROBLEM_TAG)
                .where(PROBLEM_TAG.ID.eq(id))
                .awaitBool()
        }
    }

    suspend fun addProblemTag(
        problemId: Long,
        tagId: Long
    ): Boolean {
        return dslAccess.with {
            insertInto(TAG_BELONG_PROBLEM, TAG_BELONG_PROBLEM.PROBLEM_ID, TAG_BELONG_PROBLEM.TAG_ID)
                .values(problemId, tagId)
                .onDuplicateKeyIgnore()
                .awaitBool()
        }
    }

    suspend fun removeProblemTag(
        problemId: Long,
        tagId: Long
    ): Boolean {
        return dslAccess.with {
            deleteFrom(TAG_BELONG_PROBLEM)
                .where(TAG_BELONG_PROBLEM.PROBLEM_ID.eq(problemId))
                .and(TAG_BELONG_PROBLEM.TAG_ID.eq(tagId))
                .awaitBool()
        }
    }

}