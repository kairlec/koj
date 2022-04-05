/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.records;


import com.kairlec.koj.dao.tables.Submit;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 任务表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SubmitRecord extends UpdatableRecordImpl<SubmitRecord> implements Record9<Long, Byte, String, Integer, Integer, Long, Long, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>koj.submit.id</code>. 任务id
     */
    public SubmitRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.id</code>. 任务id
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>koj.submit.state</code>. 任务状态
     */
    public SubmitRecord setState(Byte value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.state</code>. 任务状态
     */
    public Byte getState() {
        return (Byte) get(1);
    }

    /**
     * Setter for <code>koj.submit.language_id</code>. 语言id
     */
    public SubmitRecord setLanguageId(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.language_id</code>. 语言id
     */
    public String getLanguageId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>koj.submit.cast_memory</code>. 任务内存
     */
    public SubmitRecord setCastMemory(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.cast_memory</code>. 任务内存
     */
    public Integer getCastMemory() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>koj.submit.cast_time</code>. 耗时
     */
    public SubmitRecord setCastTime(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.cast_time</code>. 耗时
     */
    public Integer getCastTime() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>koj.submit.belong_competition_id</code>. 所属比赛id
     */
    public SubmitRecord setBelongCompetitionId(Long value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.belong_competition_id</code>. 所属比赛id
     */
    public Long getBelongCompetitionId() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>koj.submit.belong_user_id</code>. 所属用户id
     */
    public SubmitRecord setBelongUserId(Long value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.belong_user_id</code>. 所属用户id
     */
    public Long getBelongUserId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>koj.submit.create_time</code>. 创建时间
     */
    public SubmitRecord setCreateTime(LocalDateTime value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.create_time</code>. 创建时间
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>koj.submit.update_time</code>. 更新时间
     */
    public SubmitRecord setUpdateTime(LocalDateTime value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>koj.submit.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<Long, Byte, String, Integer, Integer, Long, Long, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<Long, Byte, String, Integer, Integer, Long, Long, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Submit.SUBMIT.ID;
    }

    @Override
    public Field<Byte> field2() {
        return Submit.SUBMIT.STATE;
    }

    @Override
    public Field<String> field3() {
        return Submit.SUBMIT.LANGUAGE_ID;
    }

    @Override
    public Field<Integer> field4() {
        return Submit.SUBMIT.CAST_MEMORY;
    }

    @Override
    public Field<Integer> field5() {
        return Submit.SUBMIT.CAST_TIME;
    }

    @Override
    public Field<Long> field6() {
        return Submit.SUBMIT.BELONG_COMPETITION_ID;
    }

    @Override
    public Field<Long> field7() {
        return Submit.SUBMIT.BELONG_USER_ID;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return Submit.SUBMIT.CREATE_TIME;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return Submit.SUBMIT.UPDATE_TIME;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Byte component2() {
        return getState();
    }

    @Override
    public String component3() {
        return getLanguageId();
    }

    @Override
    public Integer component4() {
        return getCastMemory();
    }

    @Override
    public Integer component5() {
        return getCastTime();
    }

    @Override
    public Long component6() {
        return getBelongCompetitionId();
    }

    @Override
    public Long component7() {
        return getBelongUserId();
    }

    @Override
    public LocalDateTime component8() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime component9() {
        return getUpdateTime();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Byte value2() {
        return getState();
    }

    @Override
    public String value3() {
        return getLanguageId();
    }

    @Override
    public Integer value4() {
        return getCastMemory();
    }

    @Override
    public Integer value5() {
        return getCastTime();
    }

    @Override
    public Long value6() {
        return getBelongCompetitionId();
    }

    @Override
    public Long value7() {
        return getBelongUserId();
    }

    @Override
    public LocalDateTime value8() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime value9() {
        return getUpdateTime();
    }

    @Override
    public SubmitRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public SubmitRecord value2(Byte value) {
        setState(value);
        return this;
    }

    @Override
    public SubmitRecord value3(String value) {
        setLanguageId(value);
        return this;
    }

    @Override
    public SubmitRecord value4(Integer value) {
        setCastMemory(value);
        return this;
    }

    @Override
    public SubmitRecord value5(Integer value) {
        setCastTime(value);
        return this;
    }

    @Override
    public SubmitRecord value6(Long value) {
        setBelongCompetitionId(value);
        return this;
    }

    @Override
    public SubmitRecord value7(Long value) {
        setBelongUserId(value);
        return this;
    }

    @Override
    public SubmitRecord value8(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public SubmitRecord value9(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public SubmitRecord values(Long value1, Byte value2, String value3, Integer value4, Integer value5, Long value6, Long value7, LocalDateTime value8, LocalDateTime value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SubmitRecord
     */
    public SubmitRecord() {
        super(Submit.SUBMIT);
    }

    /**
     * Create a detached, initialised SubmitRecord
     */
    public SubmitRecord(Long id, Byte state, String languageId, Integer castMemory, Integer castTime, Long belongCompetitionId, Long belongUserId, LocalDateTime createTime, LocalDateTime updateTime) {
        super(Submit.SUBMIT);

        setId(id);
        setState(state);
        setLanguageId(languageId);
        setCastMemory(castMemory);
        setCastTime(castTime);
        setBelongCompetitionId(belongCompetitionId);
        setBelongUserId(belongUserId);
        setCreateTime(createTime);
        setUpdateTime(updateTime);
    }

    /**
     * Create a detached, initialised SubmitRecord
     */
    public SubmitRecord(com.kairlec.koj.dao.tables.pojos.Submit value) {
        super(Submit.SUBMIT);

        if (value != null) {
            setId(value.getId());
            setState(value.getState());
            setLanguageId(value.getLanguageId());
            setCastMemory(value.getCastMemory());
            setCastTime(value.getCastTime());
            setBelongCompetitionId(value.getBelongCompetitionId());
            setBelongUserId(value.getBelongUserId());
            setCreateTime(value.getCreateTime());
            setUpdateTime(value.getUpdateTime());
        }
    }
}