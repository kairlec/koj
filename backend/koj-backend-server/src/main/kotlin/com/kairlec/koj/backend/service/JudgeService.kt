package com.kairlec.koj.backend.service

import com.kairlec.koj.dao.model.SubmitState

interface JudgeService {
    fun normalJudge(output: String, ansout: String): SubmitState
}