package com.kairlec.koj.core

data class State(
    val value: Int = CREATED,
    val current: Int = CREATED,
    val cause: Throwable? = null,
    val stdout: String? = null,
    val stderr: String? = null,
    val time: Long = -1,
    val memory: Long = -1,
) {
    val isError: Boolean get() = value and ERROR == ERROR
    val finished: Boolean get() = value and END == END

    fun next(stdout: String? = null, stderr: String? = null, time: Long? = null, memory: Long? = null): State {
        return State(
            (value shl 1) or value,
            current,
            stdout = stdout ?: this.stdout,
            stderr = stderr ?: this.stderr,
            time = time ?: this.time,
            memory = memory ?: this.memory
        )
    }

    fun next(value: Int, stdout: String? = null, stderr: String? = null): State {
        return State(
            this.value or value,
            value,
            stdout = stdout ?: this.stdout,
            stderr = stderr ?: this.stderr,
            time = time,
            memory = memory
        )
    }

    fun error(cause: Throwable? = null, stdout: String? = null, stderr: String? = null): State {
        return State(
            value or ERROR,
            ERROR,
            cause ?: this.cause,
            stdout ?: this.stdout,
            stderr ?: this.stdout,
            time,
            memory
        )
    }

    companion object {
        /**
         * 仅创建任务,还未初始化(比如创建临时文件夹,创建stdin文件,创建源代码文件等)
         */
        const val CREATED = 1

        /**
         * 创建号了临时文件夹,创建了stdin文件,创建了源代码文件
         */
        const val INITED = 2

        /**
         * 已经编译完成,得到了中间的输出内容
         */
        const val COMPILED = 4

        /**
         * 编译并已经运行完毕了
         */
        const val EXECUTED = 8

        /**
         * 结束,最后的内容清理完毕了(临时文件夹已经删除)
         */
        const val END = 16

        /**
         * 出错
         */
        const val ERROR = 32
    }
}
