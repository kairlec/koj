package com.kairlec.koj.dao.repository

import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.*
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.ProblemConfig
import com.kairlec.koj.dao.model.SimpleProblem
import com.kairlec.koj.dao.tables.records.ProblemConfigRecord
import com.kairlec.koj.dao.tables.records.ProblemRunRecord
import com.kairlec.koj.dao.tables.records.ProblemTagRecord
import com.kairlec.koj.dao.with
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger


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
            create.select(
                DSL.field(PROBLEM.ID.unqualifiedName),
                DSL.field(PROBLEM.NAME.unqualifiedName),
                DSL.field(PROBLEM.SPJ.unqualifiedName),
                DSL.groupConcat(DSL.field("tag_name")).separator(",").`as`("tag_names")
            )
                .from(
                    create.select(
                        PROBLEM.ID,
                        PROBLEM.NAME,
                        PROBLEM.SPJ,
                        PROBLEM_TAG.NAME.`as`("tag_name")
                    )
                        .from(PROBLEM)
                        .leftJoin(TAG_BELONG_PROBLEM)
                        .on(PROBLEM.ID.eq(TAG_BELONG_PROBLEM.PROBLEM_ID))
                        .leftJoin(PROBLEM_TAG)
                        .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                        .let {
                            if (tags.isEmpty()) {
                                it.list(PROBLEM, listCondition)
                            } else {
                                it.where(PROBLEM_TAG.NAME.`in`(tags))
                                    .list(PROBLEM, listCondition)
                            }
                        }
                ).groupBy(DSL.field(PROBLEM.ID.unqualifiedName))
                .asFlow()
                .map {
                    SimpleProblem(
                        id = it[PROBLEM.ID],
                        name = it[PROBLEM.NAME],
                        spj = it[PROBLEM.SPJ] != 0.toByte(),
                        idx = null,
                        tags = it["tag_names"]?.toString()?.split(",") ?: emptyList()
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
                DSL.field(PROBLEM.ID.unqualifiedName),
                DSL.field(PROBLEM.NAME.unqualifiedName),
                DSL.field(PROBLEM.SPJ.unqualifiedName),
                DSL.field(PROBLEM_BELONG_COMPETITION.IDX.unqualifiedName),
                DSL.groupConcat(DSL.field("tag_name")).separator(",").`as`("tag_names")
            ).from(
                create.select(
                    PROBLEM.ID,
                    PROBLEM.NAME,
                    PROBLEM.SPJ,
                    PROBLEM_BELONG_COMPETITION.IDX,
                    PROBLEM_TAG.NAME.`as`("tag_name")
                )
                    .from(PROBLEM)
                    .leftJoin(PROBLEM_BELONG_COMPETITION)
                    .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
                    .leftJoin(TAG_BELONG_PROBLEM)
                    .on(PROBLEM.ID.eq(TAG_BELONG_PROBLEM.PROBLEM_ID))
                    .leftJoin(PROBLEM_TAG)
                    .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                    .where(PROBLEM_BELONG_COMPETITION.COMPETITION_ID.eq(competitionId))
                    .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
            )
                .groupBy(DSL.field(PROBLEM.ID.unqualifiedName))
                .asFlow()
                .map {
                    SimpleProblem(
                        id = it[PROBLEM.ID],
                        name = it[PROBLEM.NAME],
                        spj = it[PROBLEM.SPJ] != 0.toByte(),
                        idx = it[PROBLEM_BELONG_COMPETITION.IDX],
                        tags = it["tag_names"]?.toString()?.split(",") ?: emptyList()
                    )
                }
        }
    }

    private val problemFields = PROBLEM.fields().map { DSL.field(it.unqualifiedName) }.toTypedArray()

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getProblem(
        id: Long
    ): Problem? {
        val record = try {
            dslAccess.with { create ->
                create.select(
                    *problemFields,
                    DSL.field(PROBLEM_BELONG_COMPETITION.IDX.unqualifiedName),
                    DSL.groupConcat(DSL.field("tag_name")).separator(",").`as`("tag_names")
                ).from(
                    create.select(
                        *PROBLEM.fields(),
                        PROBLEM_BELONG_COMPETITION.IDX,
                        PROBLEM_TAG.NAME.`as`("tag_name")
                    )
                        .from(PROBLEM)
                        .leftJoin(PROBLEM_BELONG_COMPETITION)
                        .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
                        .leftJoin(TAG_BELONG_PROBLEM)
                        .on(PROBLEM.ID.eq(TAG_BELONG_PROBLEM.PROBLEM_ID))
                        .leftJoin(PROBLEM_TAG)
                        .on(TAG_BELONG_PROBLEM.TAG_ID.eq(PROBLEM_TAG.ID))
                        .where(PROBLEM.ID.eq(id))
                        .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
                )
                    .groupBy(DSL.field(PROBLEM.ID.unqualifiedName))
                    .awaitSingle()
            }
        } catch (e: NoSuchElementException) {
            return null
        }
        return dslAccess.with { create ->
            Problem(
                id = record[PROBLEM.ID],
                name = record[PROBLEM.NAME],
                content = record[PROBLEM.CONTENT],
                spj = record[PROBLEM.SPJ] != 0.toByte(),
                createTime = record[PROBLEM.CREATE_TIME],
                updateTime = record[PROBLEM.UPDATE_TIME],
                config = dslAccess.flow {
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
                        }
                }.toList(),
                idx = record[PROBLEM_BELONG_COMPETITION.IDX],
                tags = record["tag_names"]?.toString()?.split(",") ?: emptyList()
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
                .values(name, content, if (spj) 1 else 0)
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
        tags: List<Long>?
    ): Boolean {
        if (name == null && content == null && spj == null) return false
        val ok1 = dslAccess.with { create ->
            create.update(PROBLEM)
                .setIfNotNull(PROBLEM.NAME, name)
                .setIfNotNull(PROBLEM.CONTENT, content)
                .setIfNotNull(PROBLEM.SPJ, spj?.let { if (it) 1 else 0 })
                .where(PROBLEM.ID.eq(id))
                .awaitBool()
        }
        if (tags != null) {
            val ok2 = dslAccess.with { create ->
                create.deleteFrom(PROBLEM_CONFIG)
                    .where(PROBLEM_CONFIG.PROBLEM_ID.eq(id))
                    .awaitBool()
            }
            if (ok2 && tags.isNotEmpty()) {
                dslAccess.with { create ->
                    create.insertInto(TAG_BELONG_PROBLEM, TAG_BELONG_PROBLEM.PROBLEM_ID, TAG_BELONG_PROBLEM.TAG_ID)
                        .fold(tags) {
                            values(id, it)
                        }
                        .onDuplicateKeyIgnore()
                        .awaitBool()
                }
            }
            return ok2 && ok1
        }
        return ok1
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
            val id = create.insertInto(PROBLEM_TAG, PROBLEM_TAG.NAME)
                .values(name)
                .onDuplicateKeyIgnore()
                .returningResult(PROBLEM_TAG.ID)
                .awaitOrNull()
            // 自增的,如果返回0就是冲突,自增失败
            if (id == 0L) {
                null
            } else {
                id
            }
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
    suspend fun addProblemTags(
        problemId: Long,
        tagIds: List<Long>
    ): Boolean {
        return dslAccess.with { create ->
            create.insertInto(TAG_BELONG_PROBLEM, TAG_BELONG_PROBLEM.PROBLEM_ID, TAG_BELONG_PROBLEM.TAG_ID)
                .fold(tagIds) {
                    values(problemId, it)
                }
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

    @Transactional(rollbackFor = [Exception::class])
    suspend fun saveProblemConfig(
        problemId: Long,
        languageId: String,
        time: Int,
        memory: Int,
        maxOutputSize: BigInteger?,
        maxStack: BigInteger?,
        maxProcessNumber: Int?,
        args: String,
        env: String
    ): Boolean {
        return dslAccess.with { create ->
            create.insertInto(
                PROBLEM_CONFIG,
                PROBLEM_CONFIG.PROBLEM_ID,
                PROBLEM_CONFIG.TIME,
                PROBLEM_CONFIG.MEMORY,
                PROBLEM_CONFIG.LANGUAGE_ID,
                PROBLEM_CONFIG.MAX_OUTPUT_SIZE,
                PROBLEM_CONFIG.MAX_STACK,
                PROBLEM_CONFIG.MAX_PROCESS_NUMBER,
                PROBLEM_CONFIG.ARGS,
                PROBLEM_CONFIG.ENV
            )
                .values(
                    DSL.value(problemId),
                    DSL.value(time),
                    DSL.value(memory),
                    DSL.value(languageId),
                    maxOutputSize?.let { DSL.value(org.jooq.types.ULong.valueOf(it)) }
                        ?: DSL.defaultValue(PROBLEM_CONFIG.MAX_OUTPUT_SIZE),
                    maxStack?.let { DSL.value(org.jooq.types.ULong.valueOf(it)) }
                        ?: DSL.defaultValue(PROBLEM_CONFIG.MAX_STACK),
                    maxProcessNumber?.let { DSL.value(org.jooq.types.UShort.valueOf(it)) }
                        ?: DSL.defaultValue(PROBLEM_CONFIG.MAX_PROCESS_NUMBER),
                    DSL.value(args),
                    DSL.value(env)
                )
                .onDuplicateKeyUpdate()
                .set(PROBLEM_CONFIG.TIME, time)
                .set(PROBLEM_CONFIG.MEMORY, memory)
                .set(
                    PROBLEM_CONFIG.MAX_OUTPUT_SIZE, maxOutputSize?.let { DSL.value(org.jooq.types.ULong.valueOf(it)) }
                        ?: DSL.defaultValue(PROBLEM_CONFIG.MAX_OUTPUT_SIZE)
                )
                .set(
                    PROBLEM_CONFIG.MAX_STACK, maxStack?.let { DSL.value(org.jooq.types.ULong.valueOf(it)) }
                        ?: DSL.defaultValue(PROBLEM_CONFIG.MAX_STACK)
                )
                .set(
                    PROBLEM_CONFIG.MAX_PROCESS_NUMBER,
                    maxProcessNumber?.let { DSL.value(org.jooq.types.UShort.valueOf(it)) }
                        ?: DSL.defaultValue(PROBLEM_CONFIG.MAX_PROCESS_NUMBER)
                )
                .set(PROBLEM_CONFIG.ARGS, args)
                .set(PROBLEM_CONFIG.ENV, env)
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun removeProblemConfig(
        problemId: Long,
        languageId: String,
    ): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(
                PROBLEM_CONFIG
            )
                .where(PROBLEM_CONFIG.PROBLEM_ID.eq(problemId))
                .and(PROBLEM_CONFIG.LANGUAGE_ID.eq(languageId))
                .awaitBool()
        }
    }

    @InternalApi
    suspend fun getProblemConfig(
        problemId: Long,
        languageId: String
    ): ProblemConfigRecord? {
        return dslAccess.with { create ->
            create.selectFrom(PROBLEM_CONFIG)
                .where(PROBLEM_CONFIG.PROBLEM_ID.eq(problemId))
                .and(PROBLEM_CONFIG.LANGUAGE_ID.eq(languageId))
                .awaitFirstOrNull()
        }
    }

    fun getProblemConfigs(
        problemId: Long,
    ): Flow<ProblemConfigRecord> {
        return dslAccess.flow { create ->
            create.selectFrom(PROBLEM_CONFIG)
                .where(PROBLEM_CONFIG.PROBLEM_ID.eq(problemId))
                .asFlow()
        }
    }

    /**
     * 如果不存在则新增,存在则更新
     */
    suspend fun saveProblemRunConfig(
        problemId: Long,
        stdin: String,
        ansout: String
    ): Boolean {
        return dslAccess.with { create ->
            create.insertInto(PROBLEM_RUN, PROBLEM_RUN.ID, PROBLEM_RUN.STDIN, PROBLEM_RUN.ANSOUT)
                .values(problemId, stdin, ansout)
                .onDuplicateKeyUpdate()
                .set(PROBLEM_RUN.STDIN, stdin)
                .set(PROBLEM_RUN.ANSOUT, ansout)
                .awaitBool()
        }
    }

    suspend fun getProblemRunConfig(
        problemId: Long
    ): ProblemRunRecord? {
        return dslAccess.with { create ->
            create.selectFrom(PROBLEM_RUN)
                .where(PROBLEM_RUN.ID.eq(problemId))
                .awaitFirstOrNull()
        }
    }

    @InternalApi
    suspend fun getProblemStdin(
        problemId: Long,
    ): String? {
        return dslAccess.with { create ->
            create.select(PROBLEM_RUN.STDIN)
                .from(PROBLEM_RUN)
                .where(PROBLEM_RUN.ID.eq(problemId))
                .awaitFirstOrNull()?.value1()
        }
    }

    @InternalApi
    suspend fun getProblemAnsOut(
        problemId: Long,
    ): String? {
        return dslAccess.with { create ->
            create.select(PROBLEM_RUN.ANSOUT)
                .from(PROBLEM_RUN)
                .where(PROBLEM_RUN.ID.eq(problemId))
                .awaitFirstOrNull()?.value1()
        }
    }

}