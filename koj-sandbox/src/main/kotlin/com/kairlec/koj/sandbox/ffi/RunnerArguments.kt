package com.kairlec.koj.sandbox.ffi

import jdk.incubator.foreign.CLinker.*
import jdk.incubator.foreign.MemoryAddress
import jdk.incubator.foreign.MemoryLayout
import jdk.incubator.foreign.MemorySegment
import jdk.incubator.foreign.ResourceScope.newImplicitScope
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface ValidChecker {
    fun check()
}

private inline fun <reified T : Limit<*>> default(): T {
    return when (T::class) {
        StackLimit::class -> StackLimit.default as T
        TimeLimit::class -> TimeLimit.default as T
        SizeLimit::class -> SizeLimit.default as T
        ProcessNumberLimit::class -> ProcessNumberLimit.default as T
        else -> throw IllegalStateException("Unknown limit type")
    }
}

sealed interface Limit<V : Number> {
    val value: V
}

@JvmInline
value class StackLimit(override val value: Long) : Limit<Long>, ValidChecker {
    init {
        check()
    }

    companion object {
        val default = StackLimit(32 * 1024 * 1024)
    }

    override fun check() {
        require(value > 1)
    }
}

@JvmInline
value class TimeLimit(override val value: Int) : Limit<Int>, ValidChecker {
    init {
        check()
    }

    companion object {
        val unlimited = TimeLimit(-1)
        val default = unlimited
    }

    override fun check() {
        require(value == unlimited.value || value > 1)
    }
}

@JvmInline
value class SizeLimit(override val value: Long) : Limit<Long>, ValidChecker {
    init {
        check()
    }

    companion object {
        val unlimited = SizeLimit(-1)
        val default = unlimited
    }

    override fun check() {
        require(value == unlimited.value || value > 1)
    }
}

@JvmInline
value class ProcessNumberLimit(override val value: Int) : Limit<Int>, ValidChecker {
    init {
        check()
    }

    companion object {
        val unlimited = ProcessNumberLimit(-1)
        val default = unlimited
    }

    override fun check() {
        require(value == unlimited.value || value > 1)
    }
}

data class RunnerArguments(
    val execPath: String,
    val maxCpuTime: TimeLimit = default(),
    val maxRealTime: TimeLimit = default(),
    val maxMemory: SizeLimit = default(),
    val memoryCheckOnly: Boolean = false,
    val maxStack: StackLimit = default(),
    val maxProcessNumber: ProcessNumberLimit = default(),
    val maxOutputSize: SizeLimit = default(),
    val stdinPath: String = "/dev/stdin",
    val stdoutPath: String = "/dev/stdout",
    val stderrPath: String = "/dev/stderr",
    val runArgs: List<String> = emptyList(),
    val env: List<String> = emptyList(),
    val logPath: String = "/dev/judger.log",
    val ruleName: String = "general",
    val uid: Int = 65534,
    val gid: Int = 65534,
) {
    /**
     * check constructor arguments and throw IllegalArgumentException if any of them is invalid
     */
    init {
//        require(maxCpuTime >= 1) { "maxCpuTime must be greater than 0" }
//        require(maxRealTime >= 1) { "maxRealTime must be greater than 0" }
//        require(maxStack >= 1) { "maxStack must be greater than 0" }
//        require(maxMemory >= 1) { "maxMemory must be greater than 0" }
//        require(maxProcessNumber >= 1) { "maxProcessNumber must be greater than 0" }
//        require(maxOutputSize >= 1) { "maxOutputSize must be greater than 0" }
    }

    companion object {
        private val runConfigLayOut: MemoryLayout = MemoryLayout.structLayout(
            C_INT.withName("max_cpu_time"),
            C_INT.withName("max_real_time"),
            C_LONG.withName("max_memory"),
            C_LONG.withName("max_stack"),
            C_INT.withName("max_process_number"),
            MemoryLayout.paddingLayout(32),
            C_LONG.withName("max_output_size"),
            C_INT.withName("memory_limit_check_only"),
            MemoryLayout.paddingLayout(32),
            C_POINTER.withName("exe_path"),
            C_POINTER.withName("input_path"),
            C_POINTER.withName("output_path"),
            C_POINTER.withName("error_path"),
            MemoryLayout.sequenceLayout(256, C_POINTER).withName("args"),
            MemoryLayout.sequenceLayout(256, C_POINTER).withName("env"),
            C_POINTER.withName("log_path"),
            C_POINTER.withName("seccomp_rule_name"),
            C_INT.withName("uid"),
            C_INT.withName("gid"),
        )
    }

    private fun MemorySegment.pointerSegment(): MemorySegment {
        if (!this.isNative) {
            throw IllegalStateException("MemorySegment is not native")
        }
        val pointer = MemorySegment.allocateNative(C_POINTER.byteSize(), newImplicitScope())
        val rawAddress = this.address().toRawLongValue()

        val buffer = ByteBuffer.allocate(C_POINTER.byteSize().toInt()).order(ByteOrder.nativeOrder())
        if (C_POINTER.byteSize().toInt() == Int.SIZE_BYTES) {
            buffer.putInt(rawAddress.toInt())
        } else if (C_POINTER.byteSize().toInt() == Long.SIZE_BYTES) {
            buffer.putLong(rawAddress)
        } else {
            throw IllegalStateException("Unsupported pointer size")
        }

        buffer.position(0)
        pointer.copyFrom(MemorySegment.ofByteBuffer(buffer))
        return pointer
    }


    private fun MemorySegment.set(layout: MemoryLayout, name: String, clazz: Class<*>?, value: Any) {
        layout.varHandle(clazz, MemoryLayout.PathElement.groupElement(name))
            .set(this, value)
    }

    private fun MemorySegment.setCharPointer(layout: MemoryLayout, name: String, value: String) {
        asSlice(layout.byteOffset(MemoryLayout.PathElement.groupElement(name)))
            .copyFrom(toCString(value, newImplicitScope()).pointerSegment())
    }

    private fun MemorySegment.setCharPointerList(layout: MemoryLayout, name: String, value: List<String>) {
        var segment = asSlice(layout.byteOffset(MemoryLayout.PathElement.groupElement(name)))
        value.map { toCString(it, newImplicitScope()) }.forEach {
            segment.copyFrom(it.pointerSegment())
            segment = segment.asSlice(C_POINTER.byteSize())
        }
        segment.asSlice(0, C_POINTER.byteSize()).fill(0)
    }

    private fun Boolean.asCInt() = if (this) 1 else 0

    fun asMemoryAddress(): MemoryAddress {
        val runConfig = MemorySegment.allocateNative(runConfigLayOut, newImplicitScope())
        runConfig.set(runConfigLayOut, "max_cpu_time", Int::class.javaPrimitiveType, maxCpuTime.value)
        runConfig.set(runConfigLayOut, "max_real_time", Int::class.javaPrimitiveType, maxRealTime.value)
        runConfig.set(runConfigLayOut, "max_memory", Long::class.javaPrimitiveType, maxMemory.value)
        runConfig.set(runConfigLayOut, "max_stack", Long::class.javaPrimitiveType, maxStack.value)
        runConfig.set(runConfigLayOut, "max_process_number", Int::class.javaPrimitiveType, maxProcessNumber.value)
        runConfig.set(runConfigLayOut, "max_output_size", Long::class.javaPrimitiveType, maxOutputSize.value)
        runConfig.set(
            runConfigLayOut,
            "memory_limit_check_only",
            Int::class.javaPrimitiveType,
            memoryCheckOnly.asCInt()
        )
        runConfig.setCharPointer(runConfigLayOut, "exe_path", execPath)
        runConfig.setCharPointer(runConfigLayOut, "input_path", stdinPath)
        runConfig.setCharPointer(runConfigLayOut, "output_path", stdoutPath)
        runConfig.setCharPointer(runConfigLayOut, "error_path", stderrPath)
        runConfig.setCharPointerList(runConfigLayOut, "args", runArgs)
        runConfig.setCharPointerList(runConfigLayOut, "env", env)
        runConfig.setCharPointer(runConfigLayOut, "log_path", logPath)
        runConfig.setCharPointer(runConfigLayOut, "seccomp_rule_name", ruleName)
        runConfig.set(runConfigLayOut, "uid", Int::class.javaPrimitiveType, uid)
        runConfig.set(runConfigLayOut, "gid", Int::class.javaPrimitiveType, gid)
        return runConfig.address()
    }
}
