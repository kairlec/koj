package com.kairlec.koj.judger

sealed interface Judger {
    fun judge(source: JudgerSource): JudgerResult
}

sealed interface JudgerSource

data class StandardJudgerSource(
    val output: String,
    val ans: String
) : JudgerSource

data class SpecialJudgerSource(
    val input: String,
    val output: String,
    //todo spj code
) : JudgerSource

sealed interface JudgerResult {
    val score: Int

    object AC : JudgerResult {
        override val score: Int get() = Int.MAX_VALUE
        override fun toString(): String {
            return "ac"
        }
    }

    object WA : JudgerResult {
        override val score: Int get() = Int.MIN_VALUE
        override fun toString(): String {
            return "wa"
        }
    }

    object PE : JudgerResult {
        override val score: Int get() = Int.MAX_VALUE
        override fun toString(): String {
            return "pe"
        }
    }

    class PC internal constructor(override val score: Int) : JudgerResult {
        override fun toString(): String {
            return "pc(${score})"
        }
    }

    class FAIL internal constructor(override val score: Int) : JudgerResult {
        override fun toString(): String {
            return "fail(${score})"
        }
    }

    companion object {
        fun ac() = AC
        fun wa() = WA
        fun pe() = PE
        fun pc(score: Int) = PC(score)
        fun fail(score: Int) = FAIL(score)
    }
}

object StandardJudger : Judger {
    private fun Iterator<String>.nextNotEmptyLine(): Pair<Int, String?> {
        if (!hasNext()) return 0 to null
        var line = this.next()
        var count = 0
        while (line.isEmpty()) {
            count++
            if (!this.hasNext()) {
                return count to null
            }
            line = this.next()
        }
        return Pair(count, line)
    }

    private fun String.ignoreBlank(): String {
        return this.filter { !it.isWhitespace() }
    }

    override fun judge(source: JudgerSource): JudgerResult {
        require(source is StandardJudgerSource)
        val output = source.output.lineSequence().iterator()
        val ans = source.ans.lineSequence().iterator()
        var status: JudgerResult = JudgerResult.ac()
        while (true) {
            if (!output.hasNext() && !ans.hasNext()) {
                return status
            }
            if (output.hasNext() && !ans.hasNext()) {
                val (_, str) = output.nextNotEmptyLine()
                return if (str != null && str.isNotBlank()) {
                    JudgerResult.wa()
                } else {
                    JudgerResult.pe()
                }
            }
            if (!output.hasNext() && ans.hasNext()) {
                val (_, str) = ans.nextNotEmptyLine()
                return if (str != null && str.isNotBlank()) {
                    JudgerResult.wa()
                } else {
                    JudgerResult.pe()
                }
            }
            val (outputIgnore, outputValue) = output.nextNotEmptyLine()
            val (ansIgnore, ansValue) = ans.nextNotEmptyLine()
            if (outputValue == null && ansValue == null) {
                return if (outputIgnore != ansIgnore) {
                    JudgerResult.pe()
                } else {
                    status
                }
            }
            if (outputValue == null || ansValue == null) {
                if (outputValue?.isNotBlank() == true || ansValue?.isNotBlank() == true) {
                    return JudgerResult.wa()
                } else {
                    status = JudgerResult.pe()
                    continue
                }
            }
            if (outputIgnore != ansIgnore) {
                status = JudgerResult.pe()
            }
            if (outputValue != ansValue) {
                if (outputValue.ignoreBlank() == ansValue.ignoreBlank()) {
                    status = JudgerResult.pe()
                } else {
                    return JudgerResult.wa()
                }
            }
        }
    }
}

object SpecialJudger : Judger {
    override fun judge(source: JudgerSource): JudgerResult {
        require(source is SpecialJudgerSource)
        TODO("spj")
    }
}