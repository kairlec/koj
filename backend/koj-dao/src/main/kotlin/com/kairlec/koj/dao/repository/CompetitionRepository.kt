package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.COMPETITION
import com.kairlec.koj.dao.Tables.CONTESTANTS
import com.kairlec.koj.dao.exception.CompetitionOverException
import com.kairlec.koj.dao.exception.CompetitionPwdWrongException
import com.kairlec.koj.dao.exception.NoSuchContentException
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import com.kairlec.koj.dao.with
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class CompetitionRepository(
    private val dslAccess: DSLAccess,
) {
    @Transactional(rollbackFor = [Exception::class])
    suspend fun getCompetitions(
        listCondition: ListCondition
    ): PageData<CompetitionRecord> {
        val count = dslAccess.with { create ->
            create.selectCount()
                .from(COMPETITION)
                .listCount(COMPETITION, listCondition)
                .awaitOrNull(0)
        }
        val data = dslAccess.flow { create ->
            create.selectFrom(COMPETITION)
                .list(COMPETITION, listCondition)
                .asFlow()
        }
        return data pg count
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getCompetition(
        competitionId: Long
    ): CompetitionRecord? {
        return dslAccess.with { create ->
            create.selectFrom(COMPETITION)
                .where(COMPETITION.ID.eq(competitionId))
                .awaitFirstOrNull()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun joinCompetition(
        userId: Long,
        competitionId: Long,
        pwd: String?
    ) {
        val competition = dslAccess.with { create ->
            create.selectFrom(COMPETITION)
                .where(COMPETITION.ID.eq(competitionId))
                .awaitFirstOrNull() ?: throw NoSuchContentException("cannot found competition:${competitionId}")
        }
        if (competition.isOver) {
            throw CompetitionOverException("competition is over")
        }
        if (competition.pwd != null) {
            if (pwd == null || pwd != competition.pwd) {
                throw CompetitionPwdWrongException()
            }
        }
        return dslAccess.with { create ->
            create.insertInto(CONTESTANTS, CONTESTANTS.USER_ID, CONTESTANTS.COMPETITION_ID)
                .values(userId, competitionId)
                .onDuplicateKeyIgnore()
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun createCompetition(
        name: String,
        pwd: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ): Long? {
        return dslAccess.with { create ->
            create.insertInto(
                COMPETITION,
                COMPETITION.NAME,
                COMPETITION.PWD,
                COMPETITION.START_TIME,
                COMPETITION.END_TIME
            ).values(name, pwd, startTime, endTime)
                .returningResult(COMPETITION.ID)
                .awaitOrNull()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun isInCompetition(
        userId: Long,
        competitionId: Long
    ): Boolean {
        return dslAccess.with { create ->
            create.selectCount()
                .from(CONTESTANTS)
                .where(CONTESTANTS.USER_ID.eq(userId))
                .and(CONTESTANTS.COMPETITION_ID.eq(competitionId))
                .awaitOrNull(0) > 0
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun updateCompetition(
        id: Long,
        name: String?,
        pwd: String?
    ): Boolean {
        if (name == null && pwd == null) {
            return false
        }
        return dslAccess.with { create ->
            create.update(COMPETITION)
                .let {
                    if (name != null) {
                        it.set(COMPETITION.NAME, name)
                    } else {
                        it.set(COMPETITION.PWD, pwd)
                    }
                }
                .where(COMPETITION.ID.eq(id))
                .awaitBool()
        }
    }

}


val CompetitionRecord.isOver: Boolean
    get() = endTime.isBefore(LocalDateTime.now())

val CompetitionRecord.isFreeze: Boolean
    get() = (endTime.minusHours(1)).isBefore(LocalDateTime.now())

val CompetitionRecord.isStart: Boolean
    get() = startTime.isBefore(LocalDateTime.now())

