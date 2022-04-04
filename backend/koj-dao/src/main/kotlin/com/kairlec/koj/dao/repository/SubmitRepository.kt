package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.Tables.CODE
import com.kairlec.koj.dao.Tables.SUBMIT
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.extended.awaitBool
import com.kairlec.koj.dao.extended.awaitOrNull
import com.kairlec.koj.dao.extended.list
import com.kairlec.koj.dao.model.SubmitDetail
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.tables.records.SubmitRecord
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class SubmitRepository(
    val create: DSLContext
) {
    suspend fun getSubmitRank(
        competition: Long? = null,
        listCondition: ListCondition
    ): PageData<SubmitRecord> {
        val size = create.selectCount()
            .from(SUBMIT)
            .where(SUBMIT.BELONG_COMPETITION_ID.eq(competition))
            .list(listCondition)
            .awaitOrNull(0)
        val data = create.selectFrom(SUBMIT)
            .where(SUBMIT.BELONG_COMPETITION_ID.eq(competition))
            .list(SUBMIT, listCondition)
            .asFlow()
        return data pg size
    }

    suspend fun getSubmitDetail(
        userId: Long,
        id: Long
    ): SubmitDetail? {
        val submit = create.select()
            .from(SUBMIT)
            .join(CODE)
            .on(SUBMIT.ID.eq(CODE.ID))
            .where(SUBMIT.ID.eq(id))
            .and(SUBMIT.BELONG_USER_ID.eq(userId))
            .awaitFirstOrNull() ?: return null
        return SubmitDetail(
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

    suspend fun updateSubmit(id: Long, state: SubmitState): Boolean {
        return create.update(SUBMIT)
            .set(SUBMIT.STATE, state.value)
            .where(SUBMIT.STATE.le(state.lessThan))
            .awaitBool()
    }
}