package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Hasher
import com.kairlec.koj.dao.Tables.USER
import com.kairlec.koj.dao.extended.value
import com.kairlec.koj.dao.tables.records.UserRecord
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author : Kairlec
 * @since : 2022/2/11
 **/


@Repository
class UserRepository(
    private val dslAccess: DSLAccess,
    private val hasher: Hasher,
) {
    suspend operator fun invoke(block: suspend UserRepositoryDSL.() -> Unit) {
        return UserRepositoryDSL(this).block()
    }

    @Transactional
    suspend fun <T> transaction(block: suspend UserRepositoryDSL.() -> T): T {
        return UserRepositoryDSL(this).block()
    }

    suspend fun createUser(username: String, password: String, email: String, type: UserType): Long {
        return dslAccess.withDSLContext { create ->
            create.insertInto(USER)
                .value {
                    this[USER.USERNAME] = username
                    this[USER.PASSWORD] = hasher.hash(password)
                    this[USER.EMAIL] = email
                    this[USER.TYPE] = type.value
                }
                .returningResult(USER.ID)
                .awaitSingle()
                .value1()
        }
    }

    suspend fun removeUser(username: String): Boolean {
        return dslAccess.withDSLContext { create ->
            create.deleteFrom(USER)
                .where(USER.USERNAME.eq(username))
                .awaitSingle() > 0
        }
    }

    suspend fun get(
        username: String,
        password: String? = null,
        email: String? = null,
        type: UserType? = null
    ): UserRecord? {
        val record =
            dslAccess.withDSLContext { create ->
                create.selectFrom(USER)
                    .where(USER.USERNAME.eq(username))
                    .let { if (email != null) it.and(USER.EMAIL.eq(email)) else it }
                    .let { if (type != null) it.and(USER.TYPE.eq(type.value)) else it }
                    .awaitSingle()
            }
        if (record != null && password != null) {
            if (!hasher.check(password, record.password)) {
                return null
            }
        }
        return record
    }

    suspend fun updateUser(username: String, password: String?, email: String?, type: UserType?): Boolean {
        if (password == null && email == null && type == null) {
            return false
        }
        return dslAccess.withDSLContext { create ->
            val next = create.update(USER)
                .let { if (password != null) it.set(USER.PASSWORD, hasher.hash(password)) else it }
                .let { if (email != null) it.set(USER.EMAIL, email) else it }
                .let { if (type != null) it.set(USER.TYPE, type.value) else it }

            (next as org.jooq.UpdateSetMoreStep<UserRecord>)
                .where(USER.USERNAME.eq(username))
                .awaitSingle() > 0
        }
    }
}

enum class UserType(val value: Byte) {
    ADMIN(0),
    USER(1),
}

class InitNeedProperty<T>(var value: T? = null) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("${property.name} is unset")
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

interface Executable<T> : ReadOnlyProperty<Any?, T>

class UserRepositoryDSL(
    private val userRepository: UserRepository
) {
    private val dslContext = LinkedList<Executable<*>>()

    inner class UserDSL : Executable<Mono<Boolean>> {
        internal var _username = InitNeedProperty<String>()
        var username by _username
        internal var _password = InitNeedProperty<String>()
        var password by _password
        internal var _email = InitNeedProperty<String>()
        var email by _email
        internal var _type = InitNeedProperty<UserType>()
        var type by _type

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): Mono<Boolean> {
            dslContext.remove(this)
            return mono { userRepository.updateUser(username, _password.value, _email.value, _type.value) }
        }
    }

    inner class UserQueryDSL(
        val username: String
    ) : Executable<Mono<UserRecord>> {
        internal var _password = InitNeedProperty<String>()
        var password by _password
        internal var _email = InitNeedProperty<String>()
        var email by _email
        internal var _type = InitNeedProperty<UserType>()
        var type by _type

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): Mono<UserRecord> {
            return mono { userRepository.get(username, _password.value, _email.value, _type.value) }
        }
    }

    private data class ExecutableImpl<T>(
        private val value: T
    ) : Executable<T> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }
    }

    val user: UserDSL get() = UserDSL()
    suspend fun user(block: suspend UserDSL.() -> Unit): Executable<Mono<Boolean>> {
        return user.apply { block() }.also { dslContext.add(it) }
    }

    suspend fun query(username: String, block: suspend UserQueryDSL.() -> Unit): Executable<Mono<UserRecord>> {
        return UserQueryDSL(username).apply { block() }.also { dslContext.add(it) }
    }

    suspend operator fun Executable<Mono<Boolean>>.unaryPlus(): Executable<Long> {
        require(this is UserDSL)
        dslContext.forEach {
            if (it != this && it is UserDSL) {
                userRepository.updateUser(it.username, it._password.value, it._email.value, it._type.value)
            }
        }
        dslContext.clear()
        return ExecutableImpl(userRepository.createUser(username, password, email, type))
    }

    suspend operator fun Executable<Mono<Boolean>>.unaryMinus(): Executable<Boolean> {
        require(this is UserDSL)
        dslContext.forEach {
            if (it != this && it is UserDSL) {
                userRepository.updateUser(it.username, it._password.value, it._email.value, it._type.value)
            }
        }
        dslContext.clear()
        return ExecutableImpl(userRepository.removeUser(username))
    }
}
