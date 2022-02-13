package com.kairlec.koj.sandbox.ffi.tools

import jdk.incubator.foreign.*
import jdk.incubator.foreign.CLinker.*
import java.lang.invoke.MethodType

@Suppress("unused", "SpellCheckingInspection")
object Resources {
    init {
        System.loadLibrary("judger")
    }
    object RLimitResources {
        /**
         * Address space limit.
         */
        const val RLIMIT_AS = 9

        /**
         * Largest core file that can be created, in bytes.
         */
        const val RLIMIT_CORE = 4

        /**
         * Per-process CPU limit, in seconds.
         */
        const val RLIMIT_CPU = 0

        /**
         * Maximum size of data segment, in bytes.
         */
        const val RLIMIT_DATA = 2

        /**
         * Largest file that can be created, in bytes.
         */
        const val RLIMIT_FSIZE = 1

        /**
         * Maximum number of file locks.
         */
        const val RLIMIT_LOCKS = 10

        /**
         * Locked-in-memory address space.
         */
        const val RLIMIT_MEMLOCK = 8

        /**
         * Maximum bytes in POSIX message queues.
         */
        const val RLIMIT_MSGQUEUE = 12

        /**
         * Maximum nice priority allowed to raise to.
         * Nice levels 19 .. -20 correspond to 0 .. 39
         * values of this resource limit.
         */
        const val RLIMIT_NICE = 13

        /**
         * Number of open files.
         */
        const val RLIMIT_NOFILE = 7

        /**
         * Number of processes.
         */
        const val RLIMIT_NPROC = 6

        /**
         * Largest resident set size, in bytes.
         * This affects swapping; processes that are exceeding their
         * resident set size will be more likely to have physical memory
         * taken from them.
         */
        const val RLIMIT_RSS = 5

        /**
         * Maximum realtime priority allowed for non-priviledged
         * processes.
         */
        const val RLIMIT_RTPRIO = 14

        /**
         * Maximum CPU time in µs that a process scheduled under a real-time
         * scheduling policy may consume without making a blocking system
         * call before being forcibly descheduled.
         */
        const val RLIMIT_RTTIME = 15

        /**
         * Maximum number of pending signals.
         */
        const val RLIMIT_SIGPENDING = 11

        /**
         * Maximum size of stack segment, in bytes.
         */
        const val RLIMIT_STACK = 3

        const val RLIMIT_NLIMITS = 16
    }

    /**
     * @property rlimCur The current (soft) limit.
     * @property rlimMax The hard limit.
     */
    data class Rlimit(
        val rlimCur: ULong,
        val rlimMax: ULong
    )

    private val rlimitStructMemoryLayout = MemoryLayout.structLayout(
        C_LONG.withName("rlim_cur"),
        C_LONG.withName("rlim_max")
    )

    private val rlimitResourcesMemoryLayout = MemoryLayout.structLayout(
        C_INT.withName("enumName")
    )

    private val setrlimitFunctionAddress = loader.lookup("setrlimit").get()
    private val setrlimitFunctionHandle = linker.downcallHandle(
        setrlimitFunctionAddress,
        MethodType.methodType(Int::class.javaPrimitiveType, MemorySegment::class.java, MemoryAddress::class.java),
        FunctionDescriptor.of(C_INT, rlimitResourcesMemoryLayout, C_POINTER)
    )

    fun setRLimit(resource: Int, rlimit: Rlimit): Int {
        val res = MemorySegment.allocateNative(rlimitResourcesMemoryLayout, ResourceScope.newImplicitScope())
        rlimitResourcesMemoryLayout.varHandle(Int::class.javaPrimitiveType, MemoryLayout.PathElement.groupElement("enumName"))
            .set(res, resource)
        val limit = MemorySegment.allocateNative(rlimitStructMemoryLayout, ResourceScope.newImplicitScope()).also {
            rlimitStructMemoryLayout.varHandle(Long::class.javaPrimitiveType, MemoryLayout.PathElement.groupElement("rlim_cur"))
                .set(it, rlimit.rlimCur.toLong())
            rlimitStructMemoryLayout.varHandle(Long::class.javaPrimitiveType, MemoryLayout.PathElement.groupElement("rlim_max"))
                .set(it, rlimit.rlimMax.toLong())
        }
        return setrlimitFunctionHandle.invoke(res, limit.address()) as Int
    }
}