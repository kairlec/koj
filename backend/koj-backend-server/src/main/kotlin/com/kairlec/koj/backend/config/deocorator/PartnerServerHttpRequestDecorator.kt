package com.kairlec.koj.backend.config.deocorator

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import reactor.core.publisher.Flux

class PartnerServerHttpRequestDecorator(delegate: ServerHttpRequest) : ServerHttpRequestDecorator(delegate) {
    private val _body: Flux<DataBuffer> by lazy {
        super.getBody()
    }

    override fun getBody(): Flux<DataBuffer> {
        return _body
    }
}