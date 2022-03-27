/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.CodeRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 代码表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Code extends TableImpl<CodeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.code</code>
     */
    public static final Code CODE = new Code();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CodeRecord> getRecordType() {
        return CodeRecord.class;
    }

    /**
     * The column <code>koj.code.id</code>. 代码id(和任务id一致)
     */
    public final TableField<CodeRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "代码id(和任务id一致)");

    /**
     * The column <code>koj.code.code</code>. 代码
     */
    public final TableField<CodeRecord, String> CODE_ = createField(DSL.name("code"), SQLDataType.CLOB.nullable(false), this, "代码");

    /**
     * The column <code>koj.code.create_time</code>. 创建时间
     */
    public final TableField<CodeRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "创建时间");

    private Code(Name alias, Table<CodeRecord> aliased) {
        this(alias, aliased, null);
    }

    private Code(Name alias, Table<CodeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("代码表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.code</code> table reference
     */
    public Code(String alias) {
        this(DSL.name(alias), CODE);
    }

    /**
     * Create an aliased <code>koj.code</code> table reference
     */
    public Code(Name alias) {
        this(alias, CODE);
    }

    /**
     * Create a <code>koj.code</code> table reference
     */
    public Code() {
        this(DSL.name("code"), null);
    }

    public <O extends Record> Code(Table<O> child, ForeignKey<O, CodeRecord> key) {
        super(child, key, CODE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Koj.KOJ;
    }

    @Override
    public UniqueKey<CodeRecord> getPrimaryKey() {
        return Keys.KEY_CODE_PRIMARY;
    }

    @Override
    public List<ForeignKey<CodeRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ID);
    }

    private transient Task _task;

    /**
     * Get the implicit join path to the <code>koj.task</code> table.
     */
    public Task task() {
        if (_task == null)
            _task = new Task(this, Keys.ID);

        return _task;
    }

    @Override
    public Code as(String alias) {
        return new Code(DSL.name(alias), this);
    }

    @Override
    public Code as(Name alias) {
        return new Code(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Code rename(String name) {
        return new Code(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Code rename(Name name) {
        return new Code(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, String, LocalDateTime> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
