package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Tables.*
import com.kairlec.koj.dao.exception.CompetitionOverException
import com.kairlec.koj.dao.exception.CompetitionPwdWrongException
import com.kairlec.koj.dao.exception.NoSuchContentException
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
import com.kairlec.koj.dao.model.SimpleCompetition
import com.kairlec.koj.dao.repository.UserType.ADMIN
import com.kairlec.koj.dao.with
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.kotlin.`as`
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Repository
class CompetitionRepository(
    private val dslAccess: DSLAccess,
) {
    @Transactional(rollbackFor = [Exception::class])
    suspend fun getCompetitions(
        userId: Long?,
        userType: UserType?,
        listCondition: ListCondition
    ): PageData<SimpleCompetition> {
        val count = dslAccess.with { create ->
            create.selectCount()
                .from(COMPETITION)
                .listCount(COMPETITION, listCondition)
                .awaitOrNull(0)
        }
        val data = dslAccess.flow { create ->
            // 这个不用Flux包装一次的话直接出去的Flow会调用iterator的Fetch,不知道为什么
            if (userId == null) {
                Flux.from(
                    create.selectFrom(COMPETITION)
                        .list(COMPETITION, listCondition)
                ).asFlow().map {
                    if (userType == ADMIN) {
                        SimpleCompetition(
                            it[COMPETITION.ID],
                            it[COMPETITION.NAME],
                            it[COMPETITION.START_TIME],
                            it[COMPETITION.END_TIME],
                            false,
                            it[COMPETITION.PWD].let {
                                if (it.isNullOrBlank()) {
                                    null
                                } else {
                                    it
                                }
                            },
                        )
                    } else {
                        SimpleCompetition(
                            it[COMPETITION.ID],
                            it[COMPETITION.NAME],
                            it[COMPETITION.START_TIME],
                            it[COMPETITION.END_TIME],
                            false,
                            it[COMPETITION.PWD].let {
                                if (it.isNullOrBlank()) {
                                    null
                                } else {
                                    ""
                                }
                            },
                        )
                    }
                }
            } else {
                Flux.from(
                    create.select(
                        *COMPETITION.fields(),
                        create.select(CONTESTANTS.USER_ID).from(CONTESTANTS).where(
                            CONTESTANTS.COMPETITION_ID.eq(COMPETITION.ID).and(CONTESTANTS.USER_ID.eq(userId))
                        ).limit(1).`as`("joined")
                    )
                        .from(COMPETITION)
                        .list(COMPETITION, listCondition)
                ).asFlow().map {
                    if (userType == ADMIN) {
                        SimpleCompetition(
                            it[COMPETITION.ID],
                            it[COMPETITION.NAME],
                            it[COMPETITION.START_TIME],
                            it[COMPETITION.END_TIME],
                            it["joined"] != null,
                            it[COMPETITION.PWD].let {
                                if (it.isNullOrBlank()) {
                                    null
                                } else {
                                    it
                                }
                            },
                        )
                    } else {
                        SimpleCompetition(
                            it[COMPETITION.ID],
                            it[COMPETITION.NAME],
                            it[COMPETITION.START_TIME],
                            it[COMPETITION.END_TIME],
                            it["joined"] != null,
                            it[COMPETITION.PWD].let {
                                if (it.isNullOrBlank()) {
                                    null
                                } else {
                                    ""
                                }
                            },
                        )
                    }
                }
            }
        }
        return data pg count
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun getCompetition(
        userType: UserType?,
        competitionId: Long
    ): SimpleCompetition? {
        return dslAccess.with { create ->
            create.selectFrom(COMPETITION)
                .where(COMPETITION.ID.eq(competitionId))
                .awaitFirstOrNull()?.let {
                    SimpleCompetition(
                        it[COMPETITION.ID],
                        it[COMPETITION.NAME],
                        it[COMPETITION.START_TIME],
                        it[COMPETITION.END_TIME],
                        false,
                        it[COMPETITION.PWD].let {
                            if (it.isNullOrBlank()) {
                                null
                            } else {
                                if (userType == ADMIN) {
                                    it
                                } else {
                                    ""
                                }
                            }
                        },
                    )
                }
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun deleteCompetition(
        competitionId: Long
    ): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(COMPETITION)
                .where(COMPETITION.ID.eq(competitionId))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun joinCompetition(
        userId: Long,
        competitionId: Long,
        pwd: String?
    ) {
        val competition = getCompetition(null, competitionId)
            ?: throw NoSuchContentException("cannot found competition:${competitionId}")
        if (competition.isOver) {
            throw CompetitionOverException("competition is over")
        }
        if (!competition.pwd.isNullOrBlank()) {
            if (pwd != competition.pwd) {
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
        val competition = getCompetition(null, id) ?: throw NoSuchContentException("cannot found competition:${id}")
        if (competition.isOver) {
            throw CompetitionOverException("competition is over")
        }
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

    @Transactional(rollbackFor = [Exception::class])
    suspend fun addCompetitionProblem(
        id: Long,
        problemId: Long,
    ): Boolean {
        return dslAccess.with { create ->
            create.insertInto(PROBLEM_BELONG_COMPETITION)
                .columns(PROBLEM_BELONG_COMPETITION.COMPETITION_ID, PROBLEM_BELONG_COMPETITION.PROBLEM_ID)
                .values(id, problemId)
                .onDuplicateKeyIgnore()
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun deleteCompetitionProblem(
        id: Long,
        problemId: Long,
    ): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(PROBLEM_BELONG_COMPETITION)
                .where(PROBLEM_BELONG_COMPETITION.COMPETITION_ID.eq(id))
                .and(PROBLEM_BELONG_COMPETITION.PROBLEM_ID.eq(problemId))
                .awaitBool()
        }
    }

}

private val now
    get() = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()

val SimpleCompetition.isOver: Boolean
    get() = endTime.isBefore(now)

val SimpleCompetition.isFreeze: Boolean
    get() = (endTime.minusHours(1)).isBefore(now)

val SimpleCompetition.isStart: Boolean
    get() = startTime.isBefore(now)

