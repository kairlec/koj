package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.Hasher
import com.kairlec.koj.dao.Tables.COMPETITION
import com.kairlec.koj.dao.Tables.CONTESTANTS
import com.kairlec.koj.dao.exception.CompetitionPwdWrongException
import com.kairlec.koj.dao.exception.NoSuchContentException
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.extended.awaitBool
import com.kairlec.koj.dao.extended.awaitOrNull
import com.kairlec.koj.dao.extended.list
import com.kairlec.koj.dao.tables.records.CompetitionRecord
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class CompetitionRepository(
    private val create: DSLContext,
    private val hasher: Hasher,
) {
    suspend fun getCompetitions(
        listCondition: ListCondition
    ): PageData<CompetitionRecord> {
        val data = create.selectFrom(COMPETITION)
            .list(COMPETITION, listCondition)
            .asFlow()
        val count = create.selectCount()
            .from(COMPETITION)
            .list(listCondition)
            .awaitOrNull(0)
        return data pg count
    }

    suspend fun joinCompetition(
        userId: Long,
        competitionId: Long,
        pwd: String?
    ) {
        val competition = create.selectFrom(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .awaitFirstOrNull() ?: throw NoSuchContentException("cannot found competition:${competitionId}")
        if (competition.pwd != null) {
            if (pwd == null) {
                throw CompetitionPwdWrongException()
            }
            if (!hasher.check(pwd, competition.pwd)) {
                throw CompetitionPwdWrongException()
            }
        }
        create.insertInto(CONTESTANTS, CONTESTANTS.USER_ID, CONTESTANTS.COMPETITION_ID)
            .values(userId, competitionId)
            .onDuplicateKeyIgnore()
            .awaitBool()
    }

    suspend fun createCompetition(
        name: String,
        pwd: String?
    ): Boolean {
        return create.insertInto(COMPETITION, COMPETITION.NAME, COMPETITION.PWD)
            .values(name, pwd?.let { hasher.hash(it) })
            .awaitBool()
    }

    suspend fun isInCompetition(
        userId: Long,
        competitionId: Long
    ): Boolean {
        return create.selectCount()
            .from(CONTESTANTS)
            .where(CONTESTANTS.USER_ID.eq(userId))
            .and(CONTESTANTS.COMPETITION_ID.eq(competitionId))
            .awaitOrNull(0) > 0
    }

}