package com.kairlec.koj.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.koj.backend.service.JwtService
import com.kairlec.koj.backend.util.X_IDENTITY
import com.kairlec.koj.common.exception.GlobalException
import com.kairlec.koj.common.model.asErrorResult
import com.kairlec.koj.dao.repository.UserType
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

const val userIdAttributes = "__user_id"
const val userTypeAttributes = "__user_type"

@Component
class LoginWebFilter(
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = exchange.request.headers.getFirst(X_IDENTITY)
        return if (token == null) {
            if (antPathMatcher.match(publicPath, exchange.request.uri.path)) {
                chain.filter(exchange).contextWrite {
                    it.put(ServerWebExchange::class.java, exchange)
                }
            } else {
                val resp = exchange.response
                resp.statusCode = HttpStatus.UNAUTHORIZED
                resp.setComplete()
            }
        } else {
            try {
                val (userId, userType) = jwtService.parseToken(token)
                if (antPathMatcher.match(adminPath, exchange.request.uri.path) && userType != UserType.ADMIN) {
                    val resp = exchange.response
                    resp.statusCode = HttpStatus.FORBIDDEN
                    resp.setComplete()
                } else {
                    exchange.attributes[userIdAttributes] = userId
                    exchange.attributes[userTypeAttributes] = userType
                    chain.filter(exchange).contextWrite {
                        it.put(ServerWebExchange::class.java, exchange)
                    }
                }
            } catch (e: GlobalException) {
                val resp = exchange.response
                val errorResult = e.errorCode.asErrorResult()
                resp.statusCode = HttpStatus.valueOf(errorResult.httpStatusCode.code)
                resp.headers.contentType = MediaType.APPLICATION_JSON
                resp.writeWith(Mono.just(resp.bufferFactory().wrap(objectMapper.writeValueAsBytes(errorResult))))
            } catch (e: Exception) {
                val resp = exchange.response
                resp.statusCode = HttpStatus.FORBIDDEN
                resp.setComplete()
            }
        }
    }

    companion object {
        val antPathMatcher = AntPathMatcher()
        const val publicPath = "/public/**"
        const val adminPath = "/admin/**"
    }
}