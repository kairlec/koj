package com.kairlec.koj.backend.config

import com.kairlec.koj.backend.config.deocorator.PayloadServerWebExchangeDecorator
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
class RequestBodyMultiReadableFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val payloadServerWebExchangeDecorator = PayloadServerWebExchangeDecorator(exchange)
        val mediaType = exchange.request.headers.contentType
        return if (
            MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType) ||
            MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)
        ) {
            payloadServerWebExchangeDecorator.formData.flatMap { formData ->
                payloadServerWebExchangeDecorator.attributes[FORM_DATA_ATTR] = formData
                chain.filter(payloadServerWebExchangeDecorator)
            }
        } else chain.filter(payloadServerWebExchangeDecorator)
    }

    companion object {
        const val FORM_DATA_ATTR = "fromData"
    }
}