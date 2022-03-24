package com.kairlec.koj.dao

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component

/**
 * @author : Kairlec
 * @since : 2022/2/24
 **/
@Component
internal class BCryptHasher : Hasher {
    override fun check(raw: String, hashed: String): Boolean {
        return BCrypt.checkpw(raw, hashed)
    }

    override fun hash(raw: String): String {
        return BCrypt.hashpw(raw, BCrypt.gensalt())
    }
}