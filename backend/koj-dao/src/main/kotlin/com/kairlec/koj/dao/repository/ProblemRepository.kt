package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.Tables.*
import com.kairlec.koj.dao.extended.ListCondition
import com.kairlec.koj.dao.extended.awaitOrNull
import com.kairlec.koj.dao.extended.list
import com.kairlec.koj.dao.model.Problem
import com.kairlec.koj.dao.model.ProblemConfig
import com.kairlec.koj.dao.model.SimpleProblem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class ProblemRepository(
    private val create: DSLContext
) {

    suspend fun getProblems(
        listCondition: ListCondition
    ): PageData<SimpleProblem> {
        val data = create.select(PROBLEM.ID, PROBLEM.NAME, PROBLEM.SPJ)
            .from(PROBLEM)
            .list(PROBLEM, listCondition)
            .asFlow().map {
                SimpleProblem(
                    id = it[PROBLEM.ID],
                    name = it[PROBLEM.NAME],
                    spj = it[PROBLEM.SPJ],
                    idx = null
                )
            }
        val count = create.selectCount()
            .from(PROBLEM)
            .awaitOrNull(0)
        return data pg count
    }

    fun getProblems(
        competitionId: Long,
    ): Flow<SimpleProblem> {
        return create.select(PROBLEM.ID, PROBLEM.NAME, PROBLEM.SPJ, PROBLEM_BELONG_COMPETITION.IDX)
            .from(PROBLEM)
            .leftJoin(PROBLEM_BELONG_COMPETITION)
            .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
            .where(PROBLEM_BELONG_COMPETITION.COMPETITION_ID.eq(competitionId))
            .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
            .asFlow().map {
                SimpleProblem(
                    id = it[PROBLEM.ID],
                    name = it[PROBLEM.NAME],
                    spj = it[PROBLEM.SPJ],
                    idx = it[PROBLEM_BELONG_COMPETITION.IDX]
                )
            }
    }

    suspend fun getProblem(
        id: Long
    ): Problem? {
        val record = create.select()
            .from(PROBLEM)
            .leftJoin(PROBLEM_BELONG_COMPETITION)
            .on(PROBLEM.ID.eq(PROBLEM_BELONG_COMPETITION.PROBLEM_ID))
            .where(PROBLEM.ID.eq(id))
            .orderBy(PROBLEM_BELONG_COMPETITION.CREATE_TIME)
            .awaitFirstOrNull() ?: return null
        return Problem(
            id = record[PROBLEM.ID],
            name = record[PROBLEM.NAME],
            content = record[PROBLEM.CONTENT],
            spj = record[PROBLEM.SPJ],
            createTime = record[PROBLEM.CREATE_TIME],
            updateTime = record[PROBLEM.UPDATE_TIME],
            config = create.selectFrom(PROBLEM_CONFIG)
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
            idx = record[PROBLEM_BELONG_COMPETITION.IDX]
        )
    }

}