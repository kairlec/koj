package com.kairlec.koj.backend.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.jooq.types.UInteger
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.jackson.JsonComponentModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfig {
    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(
        jsonComponentModule: JsonComponentModule,
    ): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder ->
            jacksonObjectMapperBuilder.findModulesViaServiceLoader(true)
            jacksonObjectMapperBuilder.modules(
                kotlinModule(),
                JavaTimeModule(),
                SimpleModule().addSerializer(Long::class.javaObjectType, ToStringSerializer.instance)
                    .addSerializer(Long::class.javaPrimitiveType, ToStringSerializer.instance)
                    .addSerializer(UInt::class.java, ToStringSerializer.instance)
                    .addSerializer(UInteger::class.java, ToStringSerializer.instance)
                    .addSerializer(ULong::class.java, ToStringSerializer.instance)
                    .addSerializer(org.jooq.types.ULong::class.java, ToStringSerializer.instance)
                    .addDeserializer(ULong::class.java, ULongDeserializer),
                jsonComponentModule,
                ParameterNamesModule(),
                Jdk8Module()
            )
        }
    }
}

object ULongDeserializer : StdDeserializer<ULong>(ULong::class.java) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): ULong =
        parser.valueAsString.toULong()
}
