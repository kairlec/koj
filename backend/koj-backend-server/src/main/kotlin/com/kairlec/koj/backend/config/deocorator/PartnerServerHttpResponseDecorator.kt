package com.kairlec.koj.backend.config.deocorator

import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator

class PartnerServerHttpResponseDecorator(delegate: ServerHttpResponse) : ServerHttpResponseDecorator(delegate)

// 暂时没想到这里要装饰什么