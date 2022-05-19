package com.kairlec.koj.backend.service.impl

import com.kairlec.koj.backend.service.JudgeService
import com.kairlec.koj.dao.model.SubmitState
import org.springframework.stereotype.Service
import java.io.Reader

sealed class Content {
    abstract val value: String
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Content) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class SpaceContent(override val value: String) : Content() {
    val count = value.length
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpaceContent) return false
        if (!super.equals(other)) return false

        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + count
        return result
    }

}

class NewLineContent(override val value: String) : Content() {
    val count = value.length
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NewLineContent) return false
        if (!super.equals(other)) return false

        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + count
        return result
    }

}

class AnsContent(override val value: String) : Content()

class JudgeReader(private val reader: Reader) {
    private var current: Int = reader.read()
    private val contentBuilder = StringBuilder()

    fun next(): Content? {
        if (current == -1) {
            return null
        }
        contentBuilder.append(current.toChar())
        var next = reader.read()
        while (next != -1) {
            when (current) {
                ' '.code -> {
                    if (next == ' '.code) {
                        contentBuilder.append(next.toChar())
                    } else {
                        break
                    }
                }
                '\n'.code -> {
                    if (next == '\n'.code) {
                        contentBuilder.append(next.toChar())
                    } else {
                        break
                    }
                }
                else -> {
                    if (next == ' '.code || next == '\n'.code) {
                        break
                    } else {
                        contentBuilder.append(next.toChar())
                    }
                }
            }
            next = reader.read()
        }
        val content = when (current) {
            ' '.code -> {
                SpaceContent(contentBuilder.toString())
            }
            '\n'.code -> {
                NewLineContent(contentBuilder.toString())
            }
            else -> {
                AnsContent(contentBuilder.toString())
            }
        }
        current = next
        contentBuilder.setLength(0)
        return content
    }
}

private fun Reader.judge() = JudgeReader(this)

@Service
class JudgeServiceImpl : JudgeService {
    override fun normalJudge(output: String, ansout: String): SubmitState {
        val ansReader = ansout.reader().judge()
        val outputReader = output.reader().judge()
        var state = SubmitState.ACCEPTED
        var ans = ansReader.next()
        var out = outputReader.next()
        while (true) {
            if (ans == null && out == null) {
                return state
            }
            if (ans == null) {
                while (out != null) {
                    if (out is AnsContent) {
                        return SubmitState.WRONG_ANSWER
                    } else {
                        state = SubmitState.PRESENTATION_ERROR
                    }
                    out = outputReader.next()
                }
                return state
            }
            if (out == null) {
                while (ans != null) {
                    if (ans is AnsContent) {
                        return SubmitState.WRONG_ANSWER
                    } else {
                        state = SubmitState.PRESENTATION_ERROR
                    }
                    ans = ansReader.next()
                }
                return state
            }
            if (ans is SpaceContent && out is AnsContent) {
                state = SubmitState.PRESENTATION_ERROR
                ans = ansReader.next()
                continue
            }
            if (ans is SpaceContent && out is NewLineContent) {
                state = SubmitState.PRESENTATION_ERROR
                ans = ansReader.next()
                out = outputReader.next()
                continue
            }
            if (ans is SpaceContent && out is SpaceContent) {
                if (ans != out) {
                    state = SubmitState.PRESENTATION_ERROR
                }
                ans = ansReader.next()
                out = outputReader.next()
                continue
            }
            if (ans is NewLineContent && out is AnsContent) {
                state = SubmitState.PRESENTATION_ERROR
                ans = ansReader.next()
                continue
            }
            if (ans is NewLineContent && out is NewLineContent) {
                if (ans != out) {
                    state = SubmitState.PRESENTATION_ERROR
                }
                ans = ansReader.next()
                out = outputReader.next()
                continue
            }
            if (ans is NewLineContent && out is SpaceContent) {
                state = SubmitState.PRESENTATION_ERROR
                ans = ansReader.next()
                out = outputReader.next()
                continue
            }
            if (ans is AnsContent && out is NewLineContent) {
                state = SubmitState.PRESENTATION_ERROR
                out = outputReader.next()
                continue
            }
            if (ans is AnsContent && out is SpaceContent) {
                state = SubmitState.PRESENTATION_ERROR
                out = outputReader.next()
                continue
            }
            if (ans is AnsContent && out is AnsContent) {
                if (ans != out) {
                    return SubmitState.WRONG_ANSWER
                }
                ans = ansReader.next()
                out = outputReader.next()
                continue
            }
        }
    }
}