package com.kairlec.koj.dao.repository

import com.kairlec.koj.common.InternalApi
import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.*
import com.kairlec.koj.dao.exception.CreateCodeRecordException
import com.kairlec.koj.dao.exception.CreateSubmitException
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
import com.kairlec.koj.dao.model.SimpleSubmit
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.with
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class SubmitRepository(
    private val dslAccess: DSLAccess,
) {
    @Transactional(rollbackFor = [Exception::class])
    suspend fun getSubmitRank(
        listCondition: ListCondition
    ): PageData<SimpleSubmit> {
        val count = dslAccess.with { create ->
            create.selectCount()
                .from(SUBMIT)
                .where(SUBMIT.BELONG_COMPETITION_ID.isNull)
                .listCount(SUBMIT, listCondition)
                .awaitOrNull(0)
        }
        val data = dslAccess.flow { create ->
            create.select()
                .from(SUBMIT)
                .innerJoin(USER)
                .on(USER.ID.eq(SUBMIT.BELONG_USER_ID))
                .where(SUBMIT.BELONG_COMPETITION_ID.isNull)
                .list(SUBMIT, listCondition)
                .asFlow()
                .map { submit ->
                    SimpleSubmit(
                        id = submit[SUBMIT.ID],
                        problemId = submit[SUBMIT.PROBLEM_ID],
                        state = SubmitState.parse(submit[SUBMIT.STATE]),
                        castMemory = submit[SUBMIT.CAST_MEMORY],
                        castTime = submit[SUBMIT.CAST_TIME],
                        languageId = submit[SUBMIT.LANGUAGE_ID],
                        belongUserId = submit[SUBMIT.BELONG_USER_ID],
                        username = submit[USER.USERNAME],
                        createTime = submit[SUBMIT.CREATE_TIME],
                        updateTime = submit[SUBMIT.UPDATE_TIME]
                    )
                }
        }
        return data pg count
    }

    @Transactional(rollbackFor = [Exception::class])
    fun getSubmitRank(
        competition: Long,
    ): Flow<SimpleSubmit> {
        return dslAccess.flow { create ->
            create.select()
                .from(SUBMIT)
                .innerJoin(USER)
                .on(USER.ID.eq(SUBMIT.BELONG_USER_ID))
                .where(SUBMIT.BELONG_COMPETITION_ID.eq(competition))
                .orderBy(SUBMIT.CREATE_TIME.desc())
                .asFlow()
                .map { submit ->
                    SimpleSubmit(
                        id = submit[SUBMIT.ID],
                        problemId = submit[SUBMIT.PROBLEM_ID],
                        state = SubmitState.parse(submit[SUBMIT.STATE]),
                        castMemory = submit[SUBMIT.CAST_MEMORY],
                        castTime = submit[SUBMIT.CAST_TIME],
                        languageId = submit[SUBMIT.LANGUAGE_ID],
                        belongUserId = submit[SUBMIT.BELONG_USER_ID],
                        username = submit[USER.USERNAME],
                        createTime = submit[SUBMIT.CREATE_TIME],
                        updateTime = submit[SUBMIT.UPDATE_TIME]
                    )
                }
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getSubmitDetail(
        userId: Long,
        id: Long
    ): SubmitDetail? {
        val submit = dslAccess.with { create ->
            create.select()
                .from(SUBMIT)
                .innerJoin(SUBMIT_EXTEND)
                .on(SUBMIT.ID.eq(SUBMIT_EXTEND.ID))
                .innerJoin(USER)
                .on(USER.ID.eq(SUBMIT.BELONG_USER_ID))
                .where(SUBMIT.ID.eq(id))
                .and(SUBMIT.BELONG_USER_ID.eq(userId))
                .awaitFirstOrNull()
        } ?: return null
        val state = SubmitState.parse(submit[SUBMIT.STATE])
        val stderr = if (state == SubmitState.COMPILATION_ERROR) {
            dslAccess.with { create ->
                create.select(SUBMIT_EXTEND.STDERR)
                    .from(SUBMIT_EXTEND)
                    .where(SUBMIT_EXTEND.ID.eq(id))
                    .awaitFirstOrNull()
            }?.value1()
        } else {
            null
        }
        return SubmitDetail(
            id = submit[SUBMIT.ID],
            problemId = submit[SUBMIT.PROBLEM_ID],
            state = state,
            castMemory = submit[SUBMIT.CAST_MEMORY],
            castTime = submit[SUBMIT.CAST_TIME],
            languageId = submit[SUBMIT.LANGUAGE_ID],
            belongUserId = submit[SUBMIT.BELONG_USER_ID],
            username = submit[USER.USERNAME],
            createTime = submit[SUBMIT.CREATE_TIME],
            code = submit[SUBMIT_EXTEND.CODE],
            updateTime = submit[SUBMIT.UPDATE_TIME],
            stderr = stderr
        )
    }

    /**
     * 这个接口只能被由来自消息队列的状态机所更新
     */
    @InternalApi
    @Transactional(rollbackFor = [Exception::class])
    suspend fun updateSubmit(
        id: Long,
        state: SubmitState,
        castMemory: Long? = null,
        castTime: Long? = null,
        stderr: String? = null,
        stdout: String? = null
    ): Boolean {
        val timeOk = dslAccess.with { create ->
            create.update(SUBMIT)
                .setIfNotNull(SUBMIT.CAST_MEMORY, castMemory)
                .setIfNotNull(SUBMIT.CAST_TIME, castTime)
                .set(SUBMIT.STATE, state.value)
                .where(SUBMIT.STATE.le(state.lessThan))
                .and(SUBMIT.ID.eq(id))
                .awaitBool()
        }
        val stdOk = dslAccess.with { create ->
            create.update(SUBMIT_EXTEND)
                .setIfNotNull(SUBMIT_EXTEND.STDERR, stderr)
                .setIfNotNull(SUBMIT_EXTEND.STDOUT, stdout)
                .where(SUBMIT_EXTEND.ID.eq(id))
                .awaitBool()
        }
        return timeOk && stdOk
    }

    @InternalApi
    suspend fun getProblemIdOfSubmit(id: Long): Long? {
        return dslAccess.with { create ->
            create.select(SUBMIT.PROBLEM_ID)
                .from(SUBMIT)
                .where(SUBMIT.ID.eq(id))
                .awaitFirstOrNull()?.value1()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun createSubmit(
        id: Long,
        userId: Long,
        competition: Long?,
        languageId: String,
        problemId: Long,
        code: String
    ) {
        val submitResult = dslAccess.with { create ->
            create.insertInto(SUBMIT)
                .columns(
                    SUBMIT.ID,
                    SUBMIT.BELONG_USER_ID,
                    SUBMIT.BELONG_COMPETITION_ID,
                    SUBMIT.STATE,
                    SUBMIT.LANGUAGE_ID,
                    SUBMIT.PROBLEM_ID
                )
                .values(id, userId, competition, SubmitState.IN_QUEUE.value, languageId, problemId)
                .awaitBool()
        }
        val codeResult = dslAccess.with { create ->
            create.insertInto(SUBMIT_EXTEND)
                .columns(
                    SUBMIT_EXTEND.ID,
                    SUBMIT_EXTEND.CODE,
                    SUBMIT_EXTEND.STDOUT,
                    SUBMIT_EXTEND.STDERR
                )
                .values(id, code, "", "")
                .awaitBool()
        }
        if (!submitResult) {
            throw CreateSubmitException()
        }
        if (!codeResult) {
            throw CreateCodeRecordException()
        }
    }
}