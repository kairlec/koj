package com.kairlec.koj.util

import java.io.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.ln


/**
 * @author : Kairlec
 * @since : 2022/2/24
 **/

interface Base62 {
    /**
     * Encodes a sequence of bytes in Base62 encoding.
     *
     * @param message a byte sequence.
     * @return a sequence of Base62-encoded bytes.
     */
    fun encode(message: ByteArray): ByteArray

    /**
     * Decodes a sequence of Base62-encoded bytes.
     *
     * @param encoded a sequence of Base62-encoded bytes.
     * @return a byte sequence.
     * @throws IllegalArgumentException when `encoded` is not encoded over the Base62 alphabet.
     */
    fun decode(encoded: ByteArray): ByteArray

    companion object : Base62 by Base62Impl.InstanceOfGMP
}


/**
 * A Base62 encoder/decoder.
 *
 */
class Base62Impl private constructor(private val alphabet: ByteArray) : Base62 {
    private val lookup = ByteArray(256).apply {
        for (i in alphabet.indices) {
            this[alphabet[i].toInt()] = (i and 0xFF).toByte()
        }
    }

    /**
     * Encodes a sequence of bytes in Base62 encoding.
     *
     * @param message a byte sequence.
     * @return a sequence of Base62-encoded bytes.
     */
    override fun encode(message: ByteArray): ByteArray {
        val indices = convert(message, STANDARD_BASE, TARGET_BASE, true)
        return translate(indices, alphabet)
    }

    /**
     * Decodes a sequence of Base62-encoded bytes.
     *
     * @param encoded a sequence of Base62-encoded bytes.
     * @return a byte sequence.
     * @throws IllegalArgumentException when `encoded` is not encoded over the Base62 alphabet.
     */
    override fun decode(encoded: ByteArray): ByteArray {
        val prepared = translate(encoded, lookup)
        return convert(prepared, TARGET_BASE, STANDARD_BASE, false)
    }

    /**
     * Uses the elements of a byte array as indices to a dictionary and returns the corresponding values
     * in form of a byte array.
     */
    private fun translate(indices: ByteArray, dictionary: ByteArray): ByteArray {
        val translation = ByteArray(indices.size)
        for (i in indices.indices) {
            translation[i] = dictionary[indices[i].toInt()]
        }
        return translation
    }

    /**
     * Converts a byte array from a source base to a target base using the alphabet.
     */
    private fun convert(message: ByteArray, sourceBase: Int, targetBase: Int, smooth: Boolean): ByteArray {
        val estimatedLength = estimateOutputLength(message.size, sourceBase, targetBase)
        val out = ByteArrayOutputStream(estimatedLength)
        var source = if (smooth) {
            IntStream(message.map { it.toInt() + 128 }.toIntArray())
        } else {
            IntStream(message.map { it.toInt() }.toIntArray())
        }
        while (source.isNotEmpty()) {
            val quotient = WriteAbleIntStream(source.size)
            var remainder = 0
            for (i in source.indices) {
                val accumulator = source[i] + remainder * sourceBase
                val digit = (accumulator - accumulator % targetBase) / targetBase
                remainder = accumulator % targetBase
                if (quotient.isNotEmpty() || digit != 0) {
                    quotient.write(digit)
                }
            }
            out.write(remainder)
            source = IntStream(quotient.toIntArray())
        }

        // pad output with zeroes corresponding to the number of leading zeroes in the message
        var i = 0
        while (i < message.size - 1 && message[i].toInt() == 0) {
            out.write(0)
            i++
        }
        return if (smooth) {
            out.toByteArray().apply { reverse() }
        } else {
            val oba = out.toByteArray()
            for (index in oba.indices) {
                oba[index] = (oba[index] - 128).toByte()
            }
            oba.apply { reverse() }
        }
    }

    /**
     * Estimates the length of the output in bytes.
     */
    private fun estimateOutputLength(inputLength: Int, sourceBase: Int, targetBase: Int): Int {
        return ceil(ln(sourceBase.toDouble()) / ln(targetBase.toDouble()) * inputLength).toInt()
    }

    object CharacterSets {
        val GMP = (('0'..'9') + ('A'..'Z') + ('a'..'z')).map { it.code.toByte() }.toByteArray()
        val INVERTED = (('0'..'9') + ('a'..'z') + ('A'..'Z')).map { it.code.toByte() }.toByteArray()
    }

    companion object {
        val InstanceOfGMP = Base62Impl(CharacterSets.GMP)
        val InstanceOfINVERTED = Base62Impl(CharacterSets.INVERTED)
        private const val STANDARD_BASE = 256
        private const val TARGET_BASE = 62
    }

    private class WriteAbleIntStream(maxSize: Int) : IntStream(IntArray(maxSize)) {
        override var size: Int = 0
            private set

        fun write(it: Int) {
            array[size++] = it
        }

        override fun toIntArray(): IntArray {
            return array.copyOfRange(0, size)
        }
    }

    private open class IntStream(protected val array: IntArray) {
        open val size get() = array.size
        open val indices get() = IntRange(0, size - 1)

        operator fun get(it: Int): Int {
            return array[it]
        }

        open fun isEmpty() = size == 0
        open fun isNotEmpty() = size != 0

        open fun toIntArray(): IntArray {
            return array
        }
    }
}