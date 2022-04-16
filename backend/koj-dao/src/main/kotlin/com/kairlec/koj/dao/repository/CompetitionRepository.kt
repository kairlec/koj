package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Hasher
import com.kairlec.koj.dao.Tables.COMPETITION
import com.kairlec.koj.dao.Tables.CONTESTANTS
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

@Repository
class CompetitionRepository(
    private val dslAccess: DSLAccess,
    private val hasher: Hasher,
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
        if (competition.pwd != null) {
            if (pwd == null) {
                throw CompetitionPwdWrongException()
            }
            if (!hasher.check(pwd, competition.pwd)) {
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
        pwd: String?
    ): Boolean {
        return dslAccess.with { create ->
            create.insertInto(COMPETITION, COMPETITION.NAME, COMPETITION.PWD)
                .values(name, pwd?.let { hasher.hash(it) })
                .awaitBool()
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

}