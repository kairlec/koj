/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.records;


import com.kairlec.koj.dao.tables.Competition;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 比赛表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CompetitionRecord extends UpdatableRecordImpl<CompetitionRecord> implements Record5<Long, String, LocalDateTime, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>koj.competition.id</code>. 比赛id
     */
    public CompetitionRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>koj.competition.id</code>. 比赛id
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>koj.competition.name</code>. 比赛名称
     */
    public CompetitionRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>koj.competition.name</code>. 比赛名称
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>koj.competition.start_time</code>. 比赛开始时间
     */
    public CompetitionRecord setStartTime(LocalDateTime value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>koj.competition.start_time</code>. 比赛开始时间
     */
    public LocalDateTime getStartTime() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>koj.competition.end_time</code>. 比赛结束时间
     */
    public CompetitionRecord setEndTime(LocalDateTime value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>koj.competition.end_time</code>. 比赛结束时间
     */
    public LocalDateTime getEndTime() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>koj.competition.create_time</code>. 创建时间
     */
    public CompetitionRecord setCreateTime(LocalDateTime value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>koj.competition.create_time</code>. 创建时间
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Long, String, LocalDateTime, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Long, String, LocalDateTime, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Competition.COMPETITION.ID;
    }

    @Override
    public Field<String> field2() {
        return Competition.COMPETITION.NAME;
    }

    @Override
    public Field<LocalDateTime> field3() {
        return Competition.COMPETITION.START_TIME;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return Competition.COMPETITION.END_TIME;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Competition.COMPETITION.CREATE_TIME;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public LocalDateTime component3() {
        return getStartTime();
    }

    @Override
    public LocalDateTime component4() {
        return getEndTime();
    }

    @Override
    public LocalDateTime component5() {
        return getCreateTime();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public LocalDateTime value3() {
        return getStartTime();
    }

    @Override
    public LocalDateTime value4() {
        return getEndTime();
    }

    @Override
    public LocalDateTime value5() {
        return getCreateTime();
    }

    @Override
    public CompetitionRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public CompetitionRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public CompetitionRecord value3(LocalDateTime value) {
        setStartTime(value);
        return this;
    }

    @Override
    public CompetitionRecord value4(LocalDateTime value) {
        setEndTime(value);
        return this;
    }

    @Override
    public CompetitionRecord value5(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public CompetitionRecord values(Long value1, String value2, LocalDateTime value3, LocalDateTime value4, LocalDateTime value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CompetitionRecord
     */
    public CompetitionRecord() {
        super(Competition.COMPETITION);
    }

    /**
     * Create a detached, initialised CompetitionRecord
     */
    public CompetitionRecord(Long id, String name, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createTime) {
        super(Competition.COMPETITION);

        setId(id);
        setName(name);
        setStartTime(startTime);
        setEndTime(endTime);
        setCreateTime(createTime);
    }
}
