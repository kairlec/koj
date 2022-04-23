package com.kairlec.koj.dao.repository

import com.kairlec.koj.dao.DSLAccess
import com.kairlec.koj.dao.Hasher
import com.kairlec.koj.dao.Tables.SUBMIT
import com.kairlec.koj.dao.Tables.USER
import com.kairlec.koj.dao.extended.*
import com.kairlec.koj.dao.flow
import com.kairlec.koj.dao.model.RankInfo
import com.kairlec.koj.dao.model.SubmitState
import com.kairlec.koj.dao.model.UserStat
import com.kairlec.koj.dao.tables.User
import com.kairlec.koj.dao.tables.records.UserRecord
import com.kairlec.koj.dao.with
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.jooq.impl.DSL
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
    @Transactional(rollbackFor = [Exception::class])
    suspend operator fun invoke(block: suspend UserRepositoryDSL.() -> Unit) {
        return UserRepositoryDSL(this).block()
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun exists(usernameOrEmail: String): Boolean {
        return dslAccess.with { create ->
            create.selectCount()
                .from(USER)
                .where(USER.USERNAME.eq(usernameOrEmail))
                .or(USER.EMAIL.eq(usernameOrEmail))
                .awaitOrNull(0) > 0
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun stat(username: String): UserStat? {
        val stat = dslAccess.with { create ->
            create.select(USER.ID, USER.USERNAME, USER.CREATE_TIME)
                .from(USER)
                .where(USER.USERNAME.eq(username))
                .and(USER.TYPE.eq(UserType.USER.value))
                .awaitFirstOrNull()
        } ?: return null
        val userId = stat[USER.ID]
        return UserStat(
            id = userId,
            username = stat[USER.USERNAME],
            createTime = stat[USER.CREATE_TIME],
            submitted = dslAccess.with { create ->
                create.selectCount()
                    .from(SUBMIT)
                    .where(SUBMIT.BELONG_USER_ID.eq(userId))
                    .and(SUBMIT.BELONG_COMPETITION_ID.isNull)
                    .awaitOrNull(0)
            },
            ac = dslAccess.flow { create ->
                create.select(SUBMIT.PROBLEM_ID)
                    .from(SUBMIT)
                    .where(SUBMIT.BELONG_USER_ID.eq(userId))
                    .and(SUBMIT.BELONG_COMPETITION_ID.isNull)
                    .and(SUBMIT.STATE.eq(SubmitState.ACCEPTED.value))
                    .asFlow()
                    .map { it.value1() }
            }.toList()
        )
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun createUser(username: String, password: String, email: String, type: UserType): Long {
        return dslAccess.with { create ->
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

    @Transactional(rollbackFor = [Exception::class])
    suspend fun removeUser(username: String): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(USER)
                .where(USER.USERNAME.eq(username))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun removeUser(id: Long): Boolean {
        return dslAccess.with { create ->
            create.deleteFrom(USER)
                .where(USER.ID.eq(id))
                .awaitBool()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun gets(
        type: UserType? = null,
        listCondition: ListCondition,
    ): Flow<UserRecord> {
        return dslAccess.flux { create ->
            if (type == null) {
                create.selectFrom(USER)
                    .list(USER, listCondition)
            } else {
                create.selectFrom(USER)
                    .where(USER.TYPE.eq(type.value))
                    .list(USER, listCondition)
            }
        }.asFlow()
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun existAdminUser(): Boolean {
        return dslAccess.with { create ->
            create.selectCount()
                .from(USER)
                .where(USER.TYPE.eq(UserType.ADMIN.value))
                .limit(1)
                .awaitOrNull(0) > 0
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun get(
        username: String? = null,
        password: String? = null,
        email: String? = null,
        type: UserType? = null
    ): UserRecord? {
        require(username != null || email != null) {
            "username or email must be not null"
        }
        val record =
            dslAccess.with { create ->
                create.selectFrom(USER)
                    .let {
                        if (username != null && email != null) {
                            it.where(USER.USERNAME.eq(username))
                                .and(USER.EMAIL.eq(email))
                        } else if (username != null) {
                            it.where(USER.USERNAME.eq(username))
                        } else {
                            it.where(USER.EMAIL.eq(email))
                        }
                    }
                    .let { if (type != null) it.and(USER.TYPE.eq(type.value)) else it }
                    .awaitFirstOrNull()
            }
        if (record != null && password != null) {
            if (!hasher.check(password, record.password)) {
                return null
            }
        }
        return record
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun get(
        id: Long
    ): UserRecord? {
        return dslAccess.with { create ->
            create.selectFrom(USER)
                .where(USER.ID.eq(id))
                .awaitFirstOrNull()
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    suspend fun updateUser(id: Long, username: String?, password: String?, email: String?, type: UserType?): Boolean {
        if (username == null && password == null && email == null && type == null) {
            return false
        }
        return dslAccess.with { create ->
            val next = create.update(USER)
                .let { if (password != null) it.set(USER.PASSWORD, hasher.hash(password)) else it }
                .let { if (username != null) it.set(USER.USERNAME, username) else it }
                .let { if (email != null) it.set(USER.EMAIL, email) else it }
                .let { if (type != null) it.set(USER.TYPE, type.value) else it }
            (next as org.jooq.UpdateSetMoreStep<UserRecord>)
                .where(USER.ID.eq(id))
                .awaitSingle() > 0
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun rank(max: Int): Flow<RankInfo> {
        return dslAccess.flow { create ->
            create.select(User.USER.ID, User.USER.USERNAME, DSL.countDistinct(SUBMIT.PROBLEM_ID).`as`("acc"))
                .from(User.USER)
                .leftJoin(SUBMIT)
                .on(SUBMIT.BELONG_USER_ID.eq(User.USER.ID))
                .where(SUBMIT.BELONG_COMPETITION_ID.isNull)
                .and(SUBMIT.STATE.eq(SubmitState.ACCEPTED.value))
                .groupBy(User.USER.ID)
                .orderBy(DSL.field("acc"))
                .limit(max)
                .asFlow()
                .withIndex()
                .map { (index, record) ->
                    RankInfo(record[User.USER.ID], record[User.USER.USERNAME], index + 1, record.get("acc", Int::class.java))
                }
        }
    }
}

enum class UserType(val value: Byte) {
    ADMIN(0),
    USER(1),
    ;

    companion object {
        fun valueOf(value: Byte): UserType {
            return values().first { it.value == value }
        }
    }
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
        internal var _id = InitNeedProperty<Long>()
        var id by _id
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
            return mono { userRepository.updateUser(id, _username.value, _password.value, _email.value, _type.value) }
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
                userRepository.updateUser(
                    it.id,
                    it._username.value,
                    it._password.value,
                    it._email.value,
                    it._type.value
                )
            }
        }
        dslContext.clear()
        return ExecutableImpl(userRepository.createUser(username, password, email, type))
    }

    suspend operator fun Executable<Mono<Boolean>>.unaryMinus(): Executable<Boolean> {
        require(this is UserDSL)
        dslContext.forEach {
            if (it != this && it is UserDSL) {
                userRepository.updateUser(
                    it.id,
                    it._username.value,
                    it._password.value,
                    it._email.value,
                    it._type.value
                )
            }
        }
        dslContext.clear()
        return ExecutableImpl(userRepository.removeUser(username))
    }
}
