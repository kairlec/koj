/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Indexes;
import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.UserRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 用户表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class User extends TableImpl<UserRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.user</code>
     */
    public static final User USER = new User();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserRecord> getRecordType() {
        return UserRecord.class;
    }

    /**
     * The column <code>koj.user.id</code>. 用户id
     */
    public final TableField<UserRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "用户id");

    /**
     * The column <code>koj.user.username</code>. 用户名
     */
    public final TableField<UserRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR(64).nullable(false), this, "用户名");

    /**
     * The column <code>koj.user.password</code>. 密码(脱敏后)
     */
    public final TableField<UserRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.CHAR(64).nullable(false), this, "密码(脱敏后)");

    /**
     * The column <code>koj.user.email</code>. 用户邮箱
     */
    public final TableField<UserRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.VARCHAR(32).nullable(false), this, "用户邮箱");

    /**
     * The column <code>koj.user.type</code>. 用户类型: 0-普通用户, 1-管理员
     */
    public final TableField<UserRecord, Byte> TYPE = createField(DSL.name("type"), SQLDataType.TINYINT.nullable(false), this, "用户类型: 0-普通用户, 1-管理员");

    /**
     * The column <code>koj.user.create_time</code>. 创建时间
     */
    public final TableField<UserRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "创建时间");

    /**
     * The column <code>koj.user.update_time</code>. 更新时间
     */
    public final TableField<UserRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "更新时间");

    /**
     * The column <code>koj.user.blocked</code>. 是否被禁用: 0-未禁用, 1-禁用
     */
    public final TableField<UserRecord, Byte> BLOCKED = createField(DSL.name("blocked"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "是否被禁用: 0-未禁用, 1-禁用");

    private User(Name alias, Table<UserRecord> aliased) {
        this(alias, aliased, null);
    }

    private User(Name alias, Table<UserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("用户表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.user</code> table reference
     */
    public User(String alias) {
        this(DSL.name(alias), USER);
    }

    /**
     * Create an aliased <code>koj.user</code> table reference
     */
    public User(Name alias) {
        this(alias, USER);
    }

    /**
     * Create a <code>koj.user</code> table reference
     */
    public User() {
        this(DSL.name("user"), null);
    }

    public <O extends Record> User(Table<O> child, ForeignKey<O, UserRecord> key) {
        super(child, key, USER);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Koj.KOJ;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.USER_EMAIL_IDX, Indexes.USER_USERNAME_IDX);
    }

    @Override
    public Identity<UserRecord, Long> getIdentity() {
        return (Identity<UserRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<UserRecord> getPrimaryKey() {
        return Keys.KEY_USER_PRIMARY;
    }

    @Override
    public List<UniqueKey<UserRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_USER_USERNAME_UQ, Keys.KEY_USER_EMAIL_UQ);
    }

    @Override
    public User as(String alias) {
        return new User(DSL.name(alias), this);
    }

    @Override
    public User as(Name alias) {
        return new User(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public User rename(String name) {
        return new User(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public User rename(Name name) {
        return new User(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, String, String, String, Byte, LocalDateTime, LocalDateTime, Byte> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
