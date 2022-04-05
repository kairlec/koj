/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Indexes;
import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.CompetitionRecord;

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
 * 比赛表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Competition extends TableImpl<CompetitionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.competition</code>
     */
    public static final Competition COMPETITION = new Competition();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CompetitionRecord> getRecordType() {
        return CompetitionRecord.class;
    }

    /**
     * The column <code>koj.competition.id</code>. 比赛id
     */
    public final TableField<CompetitionRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "比赛id");

    /**
     * The column <code>koj.competition.name</code>. 比赛名称
     */
    public final TableField<CompetitionRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(64).nullable(false), this, "比赛名称");

    /**
     * The column <code>koj.competition.pwd</code>. 比赛密码(脱敏后)
     */
    public final TableField<CompetitionRecord, String> PWD = createField(DSL.name("pwd"), SQLDataType.CHAR(64).nullable(false), this, "比赛密码(脱敏后)");

    /**
     * The column <code>koj.competition.start_time</code>. 比赛开始时间
     */
    public final TableField<CompetitionRecord, LocalDateTime> START_TIME = createField(DSL.name("start_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "比赛开始时间");

    /**
     * The column <code>koj.competition.end_time</code>. 比赛结束时间
     */
    public final TableField<CompetitionRecord, LocalDateTime> END_TIME = createField(DSL.name("end_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "比赛结束时间");

    /**
     * The column <code>koj.competition.create_time</code>. 创建时间
     */
    public final TableField<CompetitionRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "创建时间");

    private Competition(Name alias, Table<CompetitionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Competition(Name alias, Table<CompetitionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("比赛表"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.competition</code> table reference
     */
    public Competition(String alias) {
        this(DSL.name(alias), COMPETITION);
    }

    /**
     * Create an aliased <code>koj.competition</code> table reference
     */
    public Competition(Name alias) {
        this(alias, COMPETITION);
    }

    /**
     * Create a <code>koj.competition</code> table reference
     */
    public Competition() {
        this(DSL.name("competition"), null);
    }

    public <O extends Record> Competition(Table<O> child, ForeignKey<O, CompetitionRecord> key) {
        super(child, key, COMPETITION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Koj.KOJ;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.COMPETITION_NAME_IDX);
    }

    @Override
    public Identity<CompetitionRecord, Long> getIdentity() {
        return (Identity<CompetitionRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<CompetitionRecord> getPrimaryKey() {
        return Keys.KEY_COMPETITION_PRIMARY;
    }

    @Override
    public Competition as(String alias) {
        return new Competition(DSL.name(alias), this);
    }

    @Override
    public Competition as(Name alias) {
        return new Competition(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Competition rename(String name) {
        return new Competition(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Competition rename(Name name) {
        return new Competition(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, String, LocalDateTime, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
