package com.kairlec.koj.backend.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.kairlec.koj.backend.service.JwtService
import com.kairlec.koj.common.exception.GlobalErrorCode
import com.kairlec.koj.common.exception.GlobalException
import com.kairlec.koj.dao.repository.UserType
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class JwtConfig {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    // 默认7天
    @Value("\${jwt.expiration:604800000}")
    private lateinit var expiration: String

    @Value("\${jwt.prefix:Bearer}")
    private lateinit var prefix: String

    @Bean
    fun jwt(): JwtService {
        return object : JwtService {
            val expireTime = expiration.toLong()
            val algorithm: Algorithm = Algorithm.HMAC256(secret)
            override fun generateToken(userId: Long, userType: UserType): String {
                try {
                    val expireDate = Date(System.currentTimeMillis() + expireTime)
                    return JWT.create()
                        .withExpiresAt(expireDate)
                        .withClaim("userId", userId)
                        .withClaim("userType", userType.value.toInt())
                        .sign(algorithm)
                } catch (e: Exception) {
                    throw JwtGenerateFailedException(cause = e)
                }
            }

            override fun parseToken(token: String): Pair<Long, UserType> {
                try {
                    JWT.require(algorithm).build().verify(token)
                } catch (e: JWTVerificationException) {
                    log.warn(e) { "jwt verify failed:${token}" }
                    throw JwtVerifyFailedException(cause = e)
                } catch (e: Exception) {
                    throw JwtOtherException(cause = e)
                }
                return try {
                    val jwt = JWT.decode(token)
                    if (jwt.expiresAt.time < System.currentTimeMillis()) {
                        throw JwtExpiredException()
                    }
                    jwt.getClaim("userId").asLong() to UserType.valueOf(jwt.getClaim("userType").asInt().toByte())
                } catch (e: JWTDecodeException) {
                    throw JwtInvalidException(cause = e)
                } catch (e: Exception) {
                    throw JwtOtherException(cause = e)
                }
            }

        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}

sealed class JwtException(
    override val errorCode: GlobalErrorCode,
    override val message: String,
    override val cause: Exception? = null,
) : GlobalException(errorCode, message, cause = cause)

class JwtGenerateFailedException(
    override val message: String = "jwt generate failed",
    override val cause: Exception? = null,
) : JwtException(GlobalErrorCode.GENERATE_TOKEN_FAILED, message, cause)

class JwtOtherException(
    override val message: String = "jwt other exception",
    override val cause: Exception? = null,
) : JwtException(GlobalErrorCode.TOKEN_ERROR, message, cause)

class JwtVerifyFailedException(
    override val message: String = "jwt verify failed",
    override val cause: Exception? = null,
) : JwtException(GlobalErrorCode.TOKEN_VERIFY_FAILED, message, cause)

class JwtExpiredException(
    override val message: String = "jwt expired",
    override val cause: Exception? = null,
) : JwtException(GlobalErrorCode.TOKEN_EXPIRED, message, cause)

class JwtInvalidException(
    override val message: String = "jwt invalid",
    override val cause: Exception? = null,
) : JwtException(GlobalErrorCode.TOKEN_INVALID, message, cause)