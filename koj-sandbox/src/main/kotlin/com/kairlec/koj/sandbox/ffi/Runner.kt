package com.kairlec.koj.sandbox.ffi

import com.kairlec.koj.sandbox.ffi.tools.Resources
import jdk.incubator.foreign.*
import jdk.incubator.foreign.CLinker.*
import java.lang.invoke.MethodType


object Runner {
    init {
        System.loadLibrary("judger")
    }

    private val linker = getInstance()
    private val loader = SymbolLookup.loaderLookup()

    private val runFunctionAddress = loader.lookup("run").get()

    fun run(runnerArguments: RunnerArguments): RunnerResult {
        val down = linker.downcallHandle(
            runFunctionAddress,
            MethodType.methodType(Void.TYPE, MemoryAddress::class.java, MemoryAddress::class.java),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER)
        )
        val result = RunnerResult.newMemoryAddress()
        down.invoke(runnerArguments.asMemoryAddress(), result)

        return RunnerResult.fromMemoryAddress(result)
    }

}

fun main() {
    println(Resources.setRLimit(Resources.RLimitResources.RLIMIT_CPU,Resources.Rlimit(2UL,2UL)))
//    println(
//        Runner.run(
//            RunnerArguments(
//                execPath = "/home/kairlec/KOJ/sample/main",
//                stdinPath = "/home/kairlec/KOJ/sample/stdin.txt",
//                stdoutPath = "/home/kairlec/KOJ/sample/stdout.txt",
//                stderrPath = "/home/kairlec/KOJ/sample/stderr.txt",
//                logPath = "/home/kairlec/KOJ/sample/log.txt",
//                ruleName = "c_cpp",
//                uid = -1,
//                gid = -1
//            )
//        )
//    )
//    Runner.ss()
}