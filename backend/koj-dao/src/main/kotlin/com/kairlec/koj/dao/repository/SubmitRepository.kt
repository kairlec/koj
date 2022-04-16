package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.CODE
import com.kairlec.koj.dao.Tables.SUBMIT
import com.kairlec.koj.dao.exception.CreateCodeRecordException
import com.kairlec.koj.dao.exception.CreateSubmitException
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.tables.records.SubmitRecord
import com.kairlec.koj.dao.with
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class SubmitRepository(
    private val dslAccess: DSLAccess,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getSubmitRank(
        competition: Long? = null,
        listCondition: ListCondition
    ): PageData<SubmitRecord> {
        val count = dslAccess.with { create ->
            create.selectCount()
                .from(SUBMIT)
                .where(SUBMIT.BELONG_COMPETITION_ID.eq(competition))
                .listCount(SUBMIT, listCondition)
                .awaitOrNull(0)
        }
        val data = dslAccess.flow { create ->
            create.selectFrom(SUBMIT)
                .where(SUBMIT.BELONG_COMPETITION_ID.eq(competition))
                .list(SUBMIT, listCondition)
                .asFlow()
        }
        return data pg count
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getSubmitDetail(
        userId: Long,
        id: Long
    ): SubmitDetail? {
        return dslAccess.with { create ->
            val submit = create.select()
                .from(SUBMIT)
                .join(CODE)
                .on(SUBMIT.ID.eq(CODE.ID))
                .where(SUBMIT.ID.eq(id))
                .and(SUBMIT.BELONG_USER_ID.eq(userId))
                .awaitFirstOrNull() ?: return@with null
            SubmitDetail(
                id = submit[SUBMIT.ID],
                state = SubmitState.parse(submit[SUBMIT.STATE]),
                castMemory = submit[SUBMIT.CAST_MEMORY],
                castTime = submit[SUBMIT.CAST_TIME],
                languageId = submit[SUBMIT.LANGUAGE_ID],
                belongCompetitionId = submit[SUBMIT.BELONG_COMPETITION_ID],
                belongUserId = submit[SUBMIT.BELONG_USER_ID],
                createTime = submit[SUBMIT.CREATE_TIME],
                code = submit[CODE.CODE_],
                updateTime = submit[SUBMIT.UPDATE_TIME]
            )
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun updateSubmit(id: Long, state: SubmitState): Boolean {
        return dslAccess.with { create ->
            create.update(SUBMIT)
                .set(SUBMIT.STATE, state.value)
                .where(SUBMIT.STATE.le(state.lessThan))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun createSubmit(id: Long, userId: Long, competition: Long?, languageId: String, code: String) {
        val submitResult = dslAccess.with { create ->
            coroutineScope.async {
                create.insertInto(SUBMIT)
                    .columns(
                        SUBMIT.ID,
                        SUBMIT.BELONG_USER_ID,
                        SUBMIT.BELONG_COMPETITION_ID,
                        SUBMIT.STATE,
                        SUBMIT.LANGUAGE_ID
                    )
                    .values(id, userId, competition, SubmitState.IN_QUEUE.value, languageId)
                    .awaitBool()
            }
        }
        val codeResult = dslAccess.with { create ->
            coroutineScope.async {
                create.insertInto(SUBMIT)
                    .columns(
                        SUBMIT.ID,
                        SUBMIT.BELONG_USER_ID,
                        SUBMIT.BELONG_COMPETITION_ID,
                        SUBMIT.STATE,
                        SUBMIT.LANGUAGE_ID
                    )
                    .values(id, userId, competition, SubmitState.IN_QUEUE.value, languageId)
                    .awaitBool()
            }
        }
        val (submitOk, codeOk) = awaitAll(submitResult, codeResult)
        if (!submitOk) {
            throw CreateSubmitException()
        }
        if (!codeOk) {
            throw CreateCodeRecordException()
        }
    }
}