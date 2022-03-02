package com.kairlec.koj.support

import com.kairlec.koj.core.ExecuteResultType
import com.kairlec.koj.sandbox.Result

fun Result.toExecuteResultType(): ExecuteResultType? {
    return when (this) {
        Result.CPU_TIME_LIMIT_EXCEEDED -> ExecuteResultType.TLE
        Result.REAL_TIME_LIMIT_EXCEEDED -> ExecuteResultType.TLE
        Result.MEMORY_LIMIT_EXCEEDED -> ExecuteResultType.MLE
        Result.RUNTIME_ERROR -> ExecuteResultType.RE
        Result.SYSTEM_ERROR -> ExecuteResultType.SE
        Result.OUTPUT_LIMIT_EXCEEDED -> ExecuteResultType.OLE
        else -> null
    }
}