package com.kairlec.koj.backend.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.jooq.JSONFormat
import org.jooq.Record
import org.springframework.boot.jackson.JsonComponent
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter

@Configuration
class JOOQRecordHttpMessageConverter(jsonFormat: JSONFormat?) : AbstractHttpMessageConverter<Record>() {
    private val jsonFormat = jsonFormat ?: JSONFormat.DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.OBJECT)
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
        record.formatJSON(outputMessage.body, jsonFormat)
    }

    companion object {
        private val SUPPORTED_MEDIA_TYPES = listOf(MediaType.APPLICATION_JSON)
    }
}

@JsonComponent
class JOOQRecordStdSerializer(jsonFormat: JSONFormat?) : StdSerializer<Record>(Record::class.java) {
    private val jsonFormat = jsonFormat ?: JSONFormat.DEFAULT_FOR_RECORDS.recordFormat(JSONFormat.RecordFormat.OBJECT)
    override fun serialize(value: Record, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeRawValue(value.formatJSON(jsonFormat))
    }

    override fun handledType(): Class<Record> {
        return Record::class.java
    }
}