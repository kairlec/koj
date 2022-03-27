/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Indexes;
import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.ProblemRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 题目表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Problem extends TableImpl<ProblemRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.problem</code>
     */
    public static final Problem PROBLEM = new Problem();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ProblemRecord> getRecordType() {
        return ProblemRecord.class;
    }

    /**
     * The column <code>koj.problem.id</code>. 题目id
     */
    public final TableField<ProblemRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "题目id");

    /**
     * The column <code>koj.problem.name</code>. 题目名称
     */
    public final TableField<ProblemRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(64).nullable(false), this, "题目名称");

    /**
     * The column <code>koj.problem.content</code>. 题目内容(压缩过)
     */
    public final TableField<ProblemRecord, String> CONTENT = createField(DSL.name("content"), SQLDataType.CLOB.nullable(false), this, "题目内容(压缩过)");

    /**
     * The column <code>koj.problem.spj</code>. 是否为spj
     */
    public final TableField<ProblemRecord, Boolean> SPJ = createField(DSL.name("spj"), SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BOOLEAN)), this, "是否为spj");

    /**
     * The column <code>koj.problem.create_time</code>. 创建时间
     */
    public final TableField<ProblemRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "创建时间");

    /**
     * The column <code>koj.problem.update_time</code>. 更新时间
     */
    public final TableField<ProblemRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "更新时间");

    private Problem(Name alias, Table<ProblemRecord> aliased) {
        this(alias, aliased, null);
    }

    private Problem(Name alias, Table<ProblemRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("题目表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.problem</code> table reference
     */
    public Problem(String alias) {
        this(DSL.name(alias), PROBLEM);
    }

    /**
     * Create an aliased <code>koj.problem</code> table reference
     */
    public Problem(Name alias) {
        this(alias, PROBLEM);
    }

    /**
     * Create a <code>koj.problem</code> table reference
     */
    public Problem() {
        this(DSL.name("problem"), null);
    }

    public <O extends Record> Problem(Table<O> child, ForeignKey<O, ProblemRecord> key) {
        super(child, key, PROBLEM);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Koj.KOJ;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.PROBLEM_NAME_IDX);
    }

    @Override
    public Identity<ProblemRecord, Long> getIdentity() {
        return (Identity<ProblemRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<ProblemRecord> getPrimaryKey() {
        return Keys.KEY_PROBLEM_PRIMARY;
    }

    @Override
    public Problem as(String alias) {
        return new Problem(DSL.name(alias), this);
    }

    @Override
    public Problem as(Name alias) {
        return new Problem(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Problem rename(String name) {
        return new Problem(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Problem rename(Name name) {
        return new Problem(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, String, Boolean, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
