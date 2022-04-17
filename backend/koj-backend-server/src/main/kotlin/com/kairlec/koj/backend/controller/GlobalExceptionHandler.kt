package com.kairlec.koj.backend.controller

import com.kairlec.koj.common.exception.GlobalException
import com.kairlec.koj.common.model.ErrorResult
import com.kairlec.koj.common.model.asErrorResult
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handleGlobalException(e: GlobalException): ErrorResult {
        log.error(e) { "GlobalExceptionHandler: ${e.message}" }
        return e.errorCode.asErrorResult()
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}

@Configuration
class ResponseWrapperConfig(
    val serverCodecConfigurer: ServerCodecConfigurer,
    val requestedContentTypeResolver: RequestedContentTypeResolver,
) {
    @Bean
    fun responseWrapper(): ResponseBodyResultHandler {
        return ResponseWrapper(
            serverCodecConfigurer.writers,
            requestedContentTypeResolver
        )
    }
}

class ResponseWrapper(writers: List<HttpMessageWriter<*>>, resolver: RequestedContentTypeResolver) :
    ResponseBodyResultHandler(writers, resolver) {
    override fun supports(result: HandlerResult): Boolean {
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun handleResult(exchange: ServerWebExchange, result: HandlerResult): Mono<Void> {
        val returnValue = result.returnValue
        if (returnValue == null || returnValue == Unit) {
            exchange.response.statusCode = HttpStatus.NO_CONTENT
            return super.handleResult(exchange, result)
        }
        if (returnValue is ResponseEntity<*>) {
            return super.handleResult(exchange, result)
        }
        if (returnValue is Mono<*>) {
            val b = mono {
                val body = returnValue.awaitSingleOrNull()
                when (body) {
                    Unit,
                    null -> {
                        exchange.response.statusCode = HttpStatus.NO_CONTENT
                    }
                    is String -> {
                        if (body.isEmpty()) {
                            exchange.response.statusCode = HttpStatus.NO_CONTENT
                        }
                    }
                    is Collection<*> -> {
                        if (body.isEmpty()) {
                            exchange.response.statusCode = HttpStatus.NO_CONTENT
                        }
                    }
                    is ErrorResult -> {
                        exchange.response.rawStatusCode = body.httpStatusCode.code
                    }
                }
                body
            }
            return writeBody(b, result.returnTypeSource, exchange)
        }
        if (returnValue is Unit) {
            exchange.response.statusCode = HttpStatus.NO_CONTENT
            return super.handleResult(exchange, result)
        }
        if (returnValue is String) {
            if (returnValue.isEmpty()) {
                exchange.response.statusCode = HttpStatus.NO_CONTENT
                return super.handleResult(exchange, result)
            }
        }
        if (returnValue is Collection<*>) {
            if (returnValue.isEmpty()) {
                exchange.response.statusCode = HttpStatus.NO_CONTENT
                return super.handleResult(exchange, result)
            }
        }
        if (returnValue is ErrorResult) {
            exchange.response.rawStatusCode = returnValue.httpStatusCode.code
            return super.handleResult(exchange, result)
        }
        return super.handleResult(exchange, result)
    }
}