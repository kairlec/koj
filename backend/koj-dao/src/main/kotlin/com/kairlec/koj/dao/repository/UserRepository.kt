package com.kairlec.koj.dao.repository

import com.baidu.fsg.uid.UidGenerator
import com.kairlec.koj.dao.Hasher
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * @author : Kairlec
 * @since : 2022/2/11
 **/
@Repository
class UserRepository(
    private val create: DSLContext,
    private val hasher: Hasher,
    private val uidProvider: UidGenerator,
    private val repositoryFixedConstant: RepositoryFixedConstant,
) {

}