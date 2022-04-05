/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Indexes;
import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.SubmitRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 任务表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Submit extends TableImpl<SubmitRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.submit</code>
     */
    public static final Submit SUBMIT = new Submit();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SubmitRecord> getRecordType() {
        return SubmitRecord.class;
    }

    /**
     * The column <code>koj.submit.id</code>. 任务id
     */
    public final TableField<SubmitRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "任务id");

    /**
     * The column <code>koj.submit.state</code>. 任务状态
     */
    public final TableField<SubmitRecord, Byte> STATE = createField(DSL.name("state"), SQLDataType.TINYINT.nullable(false), this, "任务状态");

    /**
     * The column <code>koj.submit.language_id</code>. 语言id
     */
    public final TableField<SubmitRecord, String> LANGUAGE_ID = createField(DSL.name("language_id"), SQLDataType.VARCHAR(64).nullable(false), this, "语言id");

    /**
     * The column <code>koj.submit.cast_memory</code>. 任务内存
     */
    public final TableField<SubmitRecord, Integer> CAST_MEMORY = createField(DSL.name("cast_memory"), SQLDataType.INTEGER, this, "任务内存");

    /**
     * The column <code>koj.submit.cast_time</code>. 耗时
     */
    public final TableField<SubmitRecord, Integer> CAST_TIME = createField(DSL.name("cast_time"), SQLDataType.INTEGER, this, "耗时");

    /**
     * The column <code>koj.submit.belong_competition_id</code>. 所属比赛id
     */
    public final TableField<SubmitRecord, Long> BELONG_COMPETITION_ID = createField(DSL.name("belong_competition_id"), SQLDataType.BIGINT, this, "所属比赛id");

    /**
     * The column <code>koj.submit.belong_user_id</code>. 所属用户id
     */
    public final TableField<SubmitRecord, Long> BELONG_USER_ID = createField(DSL.name("belong_user_id"), SQLDataType.BIGINT.nullable(false), this, "所属用户id");

    /**
     * The column <code>koj.submit.create_time</code>. 创建时间
     */
    public final TableField<SubmitRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "创建时间");

    /**
     * The column <code>koj.submit.update_time</code>. 更新时间
     */
    public final TableField<SubmitRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "更新时间");

    private Submit(Name alias, Table<SubmitRecord> aliased) {
        this(alias, aliased, null);
    }

    private Submit(Name alias, Table<SubmitRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("任务表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.submit</code> table reference
     */
    public Submit(String alias) {
        this(DSL.name(alias), SUBMIT);
    }

    /**
     * Create an aliased <code>koj.submit</code> table reference
     */
    public Submit(Name alias) {
        this(alias, SUBMIT);
    }

    /**
     * Create a <code>koj.submit</code> table reference
     */
    public Submit() {
        this(DSL.name("submit"), null);
    }

    public <O extends Record> Submit(Table<O> child, ForeignKey<O, SubmitRecord> key) {
        super(child, key, SUBMIT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Koj.KOJ;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.SUBMIT_BELONG_COMPETITION_ID, Indexes.SUBMIT_BELONG_USER_ID);
    }

    @Override
    public UniqueKey<SubmitRecord> getPrimaryKey() {
        return Keys.KEY_SUBMIT_PRIMARY;
    }

    @Override
    public List<ForeignKey<SubmitRecord, ?>> getReferences() {
        return Arrays.asList(Keys.SUBMIT_IBFK_1, Keys.SUBMIT_IBFK_2);
    }

    private transient Competition _competition;
    private transient User _user;

    /**
     * Get the implicit join path to the <code>koj.competition</code> table.
     */
    public Competition competition() {
        if (_competition == null)
            _competition = new Competition(this, Keys.SUBMIT_IBFK_1);

        return _competition;
    }

    /**
     * Get the implicit join path to the <code>koj.user</code> table.
     */
    public User user() {
        if (_user == null)
            _user = new User(this, Keys.SUBMIT_IBFK_2);

        return _user;
    }

    @Override
    public Submit as(String alias) {
        return new Submit(DSL.name(alias), this);
    }

    @Override
    public Submit as(Name alias) {
        return new Submit(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Submit rename(String name) {
        return new Submit(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Submit rename(Name name) {
        return new Submit(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Byte, String, Integer, Integer, Long, Long, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}