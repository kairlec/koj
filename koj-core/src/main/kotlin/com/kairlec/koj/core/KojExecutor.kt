package com.kairlec.koj.core

sealed interface KojExecuteResult

interface ExecuteSuccess : KojExecuteResult {
    val type: ExecuteResultType get() = ExecuteResultType.AC
}

enum class ExecuteResultType(
    val code: Int,
    val verdict: String,
    val indication: String,
) {
    AC(
        0,
        "Accepted",
        "The solution has produced output that the judge system or a checker program (commonly referred to as a special judge) accepts as correct."
    ),
    PE(
        1,
        "Presentation Error",
        "The solution has produced output that is correct in content but incorrect in format.",
    ),
    TLE(
        2,
        "Time Limit Exceeded",
        "he solution has run for longer time than permitted. This means either the time spent on all test cases exceeds the overall limit or that spent on a single test case exceeds the per-case limit. Note that time limits for solutions in Java are tripled. These solutions are also allowed an extra 110 ms for each test case.",
    ),
    MLE(
        3,
        "Memory Limit Exceeded",
        "The solution has consumed more memory than permitted."
    ),
    WA(
        4,
        "Wrong Answer",
        "The solution has not produced the desired output.",
    ),
    RE(
        5,
        "Runtime Error",
        "The solution has caused an unhandled exception (as defined by the runtime environment) during execution.",
    ),
    OLE(
        6,
        "Output Limit Exceeded",
        "The solution has produced excessive output."
    ),
    CE(
        7,
        "Compilation Error",
        "The solution cannot be compiled into any program runnable by the judge system."
    ),
    SE(
        8,
        "System Error",
        "The judge system has failed to run the solution."
    ),
}

interface ExecuteFailure : KojExecuteResult {
    val message: String
    val cause: Throwable?
    val failureType: ExecuteResultType
}

interface ExecutorConfig {
    val maxTime: Int
    val maxMemory: Long
    val maxStack: Long get() = -1
    val maxProcessNumber: Int get() = 1
    val maxOutputSize: Long
    val args: List<String> get() = emptyList()
    val env: List<String> get() = emptyList()
}

interface KojExecutor : LanguageSupport {
    val name: String
    context (KojContext) suspend fun execute(
        compileSuccess: CompileSuccess,
        input: String,
        config: ExecutorConfig
    ): KojExecuteResult
}