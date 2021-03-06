/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.ProblemRunRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 题目运行内容表(非spj)
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProblemRun extends TableImpl<ProblemRunRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.problem_run</code>
     */
    public static final ProblemRun PROBLEM_RUN = new ProblemRun();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ProblemRunRecord> getRecordType() {
        return ProblemRunRecord.class;
    }

    /**
     * The column <code>koj.problem_run.id</code>. 题目id
     */
    public final TableField<ProblemRunRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false), this, "题目id");

    /**
     * The column <code>koj.problem_run.stdin</code>. 输入
     */
    public final TableField<ProblemRunRecord, String> STDIN = createField(DSL.name("stdin"), SQLDataType.CLOB.nullable(false), this, "输入");

    /**
     * The column <code>koj.problem_run.ansout</code>. 答案输出(用于)
     */
    public final TableField<ProblemRunRecord, String> ANSOUT = createField(DSL.name("ansout"), SQLDataType.CLOB.nullable(false), this, "答案输出(用于)");

    /**
     * The column <code>koj.problem_run.create_time</code>. 创建时间
     */
    public final TableField<ProblemRunRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "创建时间");

    /**
     * The column <code>koj.problem_run.update_time</code>. 更新时间
     */
    public final TableField<ProblemRunRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "更新时间");

    private ProblemRun(Name alias, Table<ProblemRunRecord> aliased) {
        this(alias, aliased, null);
    }

    private ProblemRun(Name alias, Table<ProblemRunRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("题目运行内容表(非spj)"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.problem_run</code> table reference
     */
    public ProblemRun(String alias) {
        this(DSL.name(alias), PROBLEM_RUN);
    }

    /**
     * Create an aliased <code>koj.problem_run</code> table reference
     */
    public ProblemRun(Name alias) {
        this(alias, PROBLEM_RUN);
    }

    /**
     * Create a <code>koj.problem_run</code> table reference
     */
    public ProblemRun() {
        this(DSL.name("problem_run"), null);
    }

    public <O extends Record> ProblemRun(Table<O> child, ForeignKey<O, ProblemRunRecord> key) {
        super(child, key, PROBLEM_RUN);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Koj.KOJ;
    }

    @Override
    public UniqueKey<ProblemRunRecord> getPrimaryKey() {
        return Keys.KEY_PROBLEM_RUN_PRIMARY;
    }

    @Override
    public List<ForeignKey<ProblemRunRecord, ?>> getReferences() {
        return Arrays.asList(Keys.PROBLEM_RUN_FK);
    }

    private transient Problem _problem;

    /**
     * Get the implicit join path to the <code>koj.problem</code> table.
     */
    public Problem problem() {
        if (_problem == null)
            _problem = new Problem(this, Keys.PROBLEM_RUN_FK);

        return _problem;
    }

    @Override
    public ProblemRun as(String alias) {
        return new ProblemRun(DSL.name(alias), this);
    }

    @Override
    public ProblemRun as(Name alias) {
        return new ProblemRun(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ProblemRun rename(String name) {
        return new ProblemRun(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ProblemRun rename(Name name) {
        return new ProblemRun(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
