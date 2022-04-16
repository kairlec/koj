package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.*
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
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
import org.springframework.transaction.annotation.Transactional

@Repository
class ProblemRepository(
    private val dslAccess: DSLAccess,
) {

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getProblems(
        tags: List<String> = emptyList(),
        listCondition: ListCondition
    ): PageData<SimpleProblem> {
        val count = dslAccess.with { create ->
            create.selectCount()
                .from(PROBLEM)
                .listCount(PROBLEM, listCondition)
                .awaitOrNull(0)
        }
        return dslAccess.flow { create ->
            create.select(PROBLEM.ID, PROBLEM.NAME, PROBLEM.SPJ, DSL.field("tr.tag_names"))
                .from(PROBLEM)
                .innerJoin(
                    create.select(
                        TAG_BELONG_PROBLEM.PROBLEM_ID.`as`("pi"),
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
                .on(DSL.field("pi").eq(PROBLEM.ID))
                .list(PROBLEM, listCondition)
                .asFlow()
                .map {
                    SimpleProblem(
                        id = it[PROBLEM.ID],
                        name = it[PROBLEM.NAME],
                        spj = it[PROBLEM.SPJ],
                        idx = null,
                        tags = it["tr.tag_names"].toString().split(",")
                    )
                }
        } pg count
    }

    @Transactional(rollbackFor = [Exception::class])
    fun getProblems(
        competitionId: Long,
    ): Flow<SimpleProblem> {
        return dslAccess.flow { create ->
            create.select(
                PROBLEM.ID,
                PROBLEM.NAME,
                PROBLEM.SPJ,
                PROBLEM_BELONG_COMPETITION.IDX,
                DSL.field("tr.tag_names")
            )
                .from(PROBLEM)
                .leftJoin(PROBLEM_BELONG_COMPETITION)
                .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
                .innerJoin(
                    create.select(
                        TAG_BELONG_PROBLEM.PROBLEM_ID.`as`("pi"),
                        DSL.groupConcat(PROBLEM_TAG.NAME).separator(",").`as`("tag_names")
                    )
                        .from(PROBLEM_TAG)
                        .join(TAG_BELONG_PROBLEM)
                        .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                        .asTable("tr")
                )
                .on(DSL.field("pi").eq(PROBLEM.ID))
                .where(PROBLEM_BELONG_COMPETITION.COMPETITION_ID.eq(competitionId))
                .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
                .asFlow().map {
                    SimpleProblem(
                        id = it[PROBLEM.ID],
                        name = it[PROBLEM.NAME],
                        spj = it[PROBLEM.SPJ],
                        idx = it[PROBLEM_BELONG_COMPETITION.IDX],
                        tags = it["tr.tag_names"].toString().split(",")
                    )
                }
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getProblem(
        id: Long
    ): Problem? {
        val record = dslAccess.with { create ->
            create.select(*PROBLEM.fields(), *PROBLEM_BELONG_COMPETITION.fields(), DSL.field("tr.tag_names"))
                .from(PROBLEM)
                .leftJoin(PROBLEM_BELONG_COMPETITION)
                .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
                .innerJoin(
                    create.select(
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
                .awaitFirstOrNull()
        } ?: return null
        return dslAccess.with { create ->
            Problem(
                id = record[PROBLEM.ID],
                name = record[PROBLEM.NAME],
                content = record[PROBLEM.CONTENT],
                spj = record[PROBLEM.SPJ],
                createTime = record[PROBLEM.CREATE_TIME],
                updateTime = record[PROBLEM.UPDATE_TIME],
                config = dslAccess.with {
                    create.selectFrom(PROBLEM_CONFIG)
                        .where(PROBLEM_CONFIG.PROBLEM_ID.eq(id))
                        .asFlow().map {
                            ProblemConfig(
                                languageId = it[PROBLEM_CONFIG.LANGUAGE_ID],
                                memoryLimit = it[PROBLEM_CONFIG.MEMORY],
                                timeLimit = it[PROBLEM_CONFIG.TIME],
                                createTime = it[PROBLEM_CONFIG.CREATE_TIME],
                                updateTime = it[PROBLEM_CONFIG.UPDATE_TIME]
                            )
                        }.toList()
                },
                idx = record[PROBLEM_BELONG_COMPETITION.IDX],
                tags = record["tag_names"].toString().split(",")
            )
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getTags(listCondition: ListCondition): PageData<ProblemTagRecord> {
        val data = dslAccess.flow { create ->
            create.selectFrom(PROBLEM_TAG)
                .list(PROBLEM_TAG, listCondition)
                .asFlow()
        }
        val count = dslAccess.with { create ->
            create.selectCount()
                .from(PROBLEM_TAG)
                .listCount(PROBLEM_TAG, listCondition)
                .awaitOrNull(0)
        }
        return data pg count
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun newProblem(
        name: String,
        content: String,
        spj: Boolean,
    ): Long? {
        return dslAccess.with { create ->
            create.insertInto(PROBLEM, PROBLEM.NAME, PROBLEM.CONTENT, PROBLEM.SPJ)
                .values(name, content, spj)
                .returningResult(PROBLEM.ID)
                .awaitOrNull()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun updateProblem(
        id: Long,
        name: String?,
        content: String?,
        spj: Boolean?,
    ): Boolean {
        if (name == null && content == null && spj == null) return false
        return dslAccess.with { create ->
            create.update(PROBLEM)
                .setIfNotNull(PROBLEM.NAME, name)
                .setIfNotNull(PROBLEM.CONTENT, content)
                .setIfNotNull(PROBLEM.SPJ, spj)
                .where(PROBLEM.ID.eq(id))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun updateTag(
        tagId: Long,
        name: String
    ): Boolean {
        return dslAccess.with { create ->
            create.update(PROBLEM_TAG)
                .set(PROBLEM_TAG.NAME, name)
                .where(PROBLEM_TAG.ID.eq(tagId))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun removeProblem(
        id: Long
    ): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(PROBLEM)
                .where(PROBLEM.ID.eq(id))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun newTag(
        name: String
    ): Long? {
        return dslAccess.with { create ->
            create.insertInto(PROBLEM_TAG, PROBLEM_TAG.NAME)
                .values(name)
                .returningResult(PROBLEM_TAG.ID)
                .awaitOrNull()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun removeTag(
        id: Long
    ): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(PROBLEM_TAG)
                .where(PROBLEM_TAG.ID.eq(id))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun addProblemTag(
        problemId: Long,
        tagId: Long
    ): Boolean {
        return dslAccess.with { create ->
            create.insertInto(TAG_BELONG_PROBLEM, TAG_BELONG_PROBLEM.PROBLEM_ID, TAG_BELONG_PROBLEM.TAG_ID)
                .values(problemId, tagId)
                .onDuplicateKeyIgnore()
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun removeProblemTag(
        problemId: Long,
        tagId: Long
    ): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(TAG_BELONG_PROBLEM)
                .where(TAG_BELONG_PROBLEM.PROBLEM_ID.eq(problemId))
                .and(TAG_BELONG_PROBLEM.TAG_ID.eq(tagId))
                .awaitBool()
        }
    }

}