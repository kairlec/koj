package com.kairlec.koj.sandbox.ffi

import jdk.incubator.foreign.*

data class RunnerResult(
    val cpuTime: Int = 0,
    val realTime: Int = 0,
    val memory: Long = 0,
    val signal: Int = 0,
    val exitCode: Int = 0,
    val error: Int = 0,
    val result: Int = 0,
) {
    companion object {
        private val runResultLayOut: MemoryLayout = MemoryLayout.structLayout(
            CLinker.C_INT.withName("cpu_time"),
            CLinker.C_INT.withName("real_time"),
            CLinker.C_LONG.withName("memory"),
            CLinker.C_INT.withName("signal"),
            CLinker.C_INT.withName("exit_code"),
            CLinker.C_INT.withName("error"),
            CLinker.C_INT.withName("result"),
        )

        fun newMemoryAddress(): MemoryAddress {
            return MemorySegment.allocateNative(runResultLayOut, ResourceScope.newImplicitScope()).address()
        }

        private fun MemorySegment.get(layout: MemoryLayout, name: String, clazz: Class<*>?): Any {
            return layout.varHandle(clazz, MemoryLayout.PathElement.groupElement(name))
                .get(this)
        }

        fun fromMemoryAddress(address: MemoryAddress): RunnerResult {
            val segment = address.asSegment(runResultLayOut.byteSize(), ResourceScope.newImplicitScope())
            return RunnerResult(
                cpuTime = segment.get(runResultLayOut, "cpu_time", Int::class.javaPrimitiveType) as Int,
                realTime = segment.get(runResultLayOut, "real_time", Int::class.javaPrimitiveType) as Int,
                memory = segment.get(runResultLayOut, "memory", Long::class.javaPrimitiveType) as Long,
                signal = segment.get(runResultLayOut, "signal", Int::class.javaPrimitiveType) as Int,
                exitCode = segment.get(runResultLayOut, "exit_code", Int::class.javaPrimitiveType) as Int,
                error = segment.get(runResultLayOut, "error", Int::class.javaPrimitiveType) as Int,
                result = segment.get(runResultLayOut, "result", Int::class.javaPrimitiveType) as Int,
            )
        }
    }
}