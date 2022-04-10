package com.kairlec.koj.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.koj.backend.service.JwtService
import com.kairlec.koj.backend.util.X_IDENTITY
import com.kairlec.koj.common.exception.GlobalException
import com.kairlec.koj.common.model.asErrorResult
import com.kairlec.koj.dao.repository.UserType
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

const val userIdAttributes = "__user_id"

@Component
class LoginWebFilter(
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return if (antPathMatcher.match(publicPath, exchange.request.uri.path)) {
            chain.filter(exchange)
        } else {
            val token = exchange.request.headers.getFirst(X_IDENTITY)
            if (token == null) {
                val resp = exchange.response
                resp.statusCode = HttpStatus.UNAUTHORIZED
                resp.setComplete()
            } else {
                try {
                    val (userId, userType) = jwtService.parseToken(token)
                    if (antPathMatcher.match(adminPath, exchange.request.uri.path) && userType != UserType.ADMIN) {
                        val resp = exchange.response
                        resp.statusCode = HttpStatus.FORBIDDEN
                        resp.setComplete()
                    } else {
                        exchange.attributes[userIdAttributes] = userId
                        exchange.attributes[userIdAttributes] = userType
                        chain.filter(exchange)
                    }
                } catch (e: GlobalException) {
                    val resp = exchange.response
                    val errorResult = e.errorCode.asErrorResult()
                    resp.statusCode = HttpStatus.valueOf(errorResult.httpStatusCode.code)
                    resp.writeAndFlushWith {
                        it.onNext(
                            DataBufferUtils.readInputStream(
                                { objectMapper.writeValueAsBytes(errorResult).inputStream() },
                                defaultDataBufferFactory,
                                1024
                            )
                        )
                        it.onComplete()
                    }
                } catch (e: Exception) {
                    val resp = exchange.response
                    resp.statusCode = HttpStatus.FORBIDDEN
                    resp.setComplete()
                }
            }
        }
    }

    companion object {
        val defaultDataBufferFactory = DefaultDataBufferFactory()
        val antPathMatcher = AntPathMatcher()
        const val publicPath = "/public/**"
        const val adminPath = "/admin/**"
    }
}