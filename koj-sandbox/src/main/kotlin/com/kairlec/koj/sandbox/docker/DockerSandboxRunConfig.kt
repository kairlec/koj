package com.kairlec.koj.sandbox.docker

data class KOJEnv(
    val keepStdin: Boolean,
    val keepStdout: Boolean = false,
    val maxCpuTime: Int,
    val maxRealTime: Int,
    val maxMemory: Long,
    val maxStack: Long,
    val maxProcessNumber: Int,
    val maxOutputSize: Long,
    val memoryCheckOnly: Boolean,
    val exePath: String,
    val addonPath: String = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
    val args: List<String>,
    val env: List<String>,
) {
    fun asList(): List<String> {
        return buildList {
            add("KEEP_STDIN=${if (keepStdin) 1 else 0}")
            add("KEEP_STDOUT=${if (keepStdout) 1 else 0}")
            add("MAX_CPU_TIME=$maxCpuTime")
            add("MAX_REAL_TIME=$maxRealTime")
            add("MAX_MEMORY=$maxMemory")
            add("MAX_STACK=$maxStack")
            add("MAX_PROCESS_NUMBER=$maxProcessNumber")
            add("MEMORY_LIMIT_CHECK_ONLY=${if (memoryCheckOnly) 1 else 0}")
            add("MAX_OUTPUT_SIZE=$maxOutputSize")
            add("EXE_PATH=$exePath")
            add("ADDON_PATH=$addonPath")
            add("ARGS=${args.joinToString(";")}")
            add("ENV=${env.joinToString(";")}")
        }
    }
}

data class DockerSandboxRunConfig(
    val image: String,
    val namespace: String,
    val exeMount: Pair<String, String>,
    val input: String,
    val kojEnv: KOJEnv,
    val debug: Boolean
)