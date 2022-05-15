package com.kairlec.koj.backend.config.deocorator

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange

import org.springframework.web.server.ServerWebExchangeDecorator


class PayloadServerWebExchangeDecorator(delegate: ServerWebExchange) : ServerWebExchangeDecorator(delegate) {
    private val requestDecorator: PartnerServerHttpRequestDecorator =
        PartnerServerHttpRequestDecorator(delegate.request)
    private val responseDecorator: PartnerServerHttpResponseDecorator =
        PartnerServerHttpResponseDecorator(delegate.response)

    override fun getRequest(): ServerHttpRequest {
        return requestDecorator
    }

    override fun getResponse(): ServerHttpResponse {
        return responseDecorator
    }
}