package com.kairlec.koj.backend.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.readValue
import com.kairlec.koj.dao.tables.records.ProblemConfigRecord
import com.kairlec.koj.dao.tables.records.UserRecord
import org.jooq.Record
import org.springframework.boot.jackson.JsonComponent
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter

val camelCaseRegex = "_([a-zA-Z\\d])".toRegex()

val String.camelCase: String
    get() = this.replace(camelCaseRegex) {
        it.groupValues[1].uppercase()
    }


fun <V> Map<String, V>.camelCase(): Map<String, V> = this.mapKeys { it.key.camelCase }

fun ProblemConfigRecord.toMap(objectMapper: ObjectMapper): Map<String, Any> {
    return intoMap().entries.associate {
        if (it.key == "env" || it.key == "args") {
            it.key.camelCase to objectMapper.readValue<List<String>>(it.value as String)
        } else {
            it.key.camelCase to it.value
        }
    }
}

fun UserRecord.toMap(): Map<String, Any> {
    return intoMap().filterKeys { it != "password" }.camelCase()
}

@Configuration
class JOOQRecordHttpMessageConverter(private val objectMapper: ObjectMapper) : AbstractHttpMessageConverter<Record>() {
    override fun canRead(clazz: Class<*>, mediaType: MediaType?): Boolean {
        return false
    }

    override fun getSupportedMediaTypes(): List<MediaType> {
        return SUPPORTED_MEDIA_TYPES
    }

    override fun supports(clazz: Class<*>): Boolean {
        return Record::class.java.isAssignableFrom(clazz)
    }

    override fun readInternal(clazz: Class<out Record>, inputMessage: HttpInputMessage): Record {
        throw UnsupportedOperationException()
    }

    override fun writeInternal(record: Record, outputMessage: HttpOutputMessage) {
        objectMapper.writeValue(
            outputMessage.body, when (record) {
                is ProblemConfigRecord -> record.toMap(objectMapper)
                is UserRecord -> record.toMap()
                else -> record.intoMap().camelCase()
            }
        )
    }

    companion object {
        private val SUPPORTED_MEDIA_TYPES = listOf(MediaType.APPLICATION_JSON)
    }
}

@JsonComponent
class JOOQRecordStdSerializer : StdSerializer<Record>(Record::class.java) {
    override fun serialize(value: Record, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writePOJO(
            when (value) {
                is ProblemConfigRecord -> value.toMap(gen.codec as ObjectMapper)
                is UserRecord -> value.toMap()
                else -> value.intoMap().camelCase()
            }
        )
    }

    override fun handledType(): Class<Record> {
        return Record::class.java
    }
}