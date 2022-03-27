/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.records;


import com.kairlec.koj.dao.tables.Problem;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * 题目表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProblemRecord extends UpdatableRecordImpl<ProblemRecord> implements Record6<Long, String, String, Boolean, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>koj.problem.id</code>. 题目id
     */
    public ProblemRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>koj.problem.id</code>. 题目id
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>koj.problem.name</code>. 题目名称
     */
    public ProblemRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>koj.problem.name</code>. 题目名称
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>koj.problem.content</code>. 题目内容(压缩过)
     */
    public ProblemRecord setContent(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>koj.problem.content</code>. 题目内容(压缩过)
     */
    public String getContent() {
        return (String) get(2);
    }

    /**
     * Setter for <code>koj.problem.spj</code>. 是否为spj
     */
    public ProblemRecord setSpj(Boolean value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>koj.problem.spj</code>. 是否为spj
     */
    public Boolean getSpj() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>koj.problem.create_time</code>. 创建时间
     */
    public ProblemRecord setCreateTime(LocalDateTime value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>koj.problem.create_time</code>. 创建时间
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>koj.problem.update_time</code>. 更新时间
     */
    public ProblemRecord setUpdateTime(LocalDateTime value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>koj.problem.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<Long, String, String, Boolean, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<Long, String, String, Boolean, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Problem.PROBLEM.ID;
    }

    @Override
    public Field<String> field2() {
        return Problem.PROBLEM.NAME;
    }

    @Override
    public Field<String> field3() {
        return Problem.PROBLEM.CONTENT;
    }

    @Override
    public Field<Boolean> field4() {
        return Problem.PROBLEM.SPJ;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return Problem.PROBLEM.CREATE_TIME;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return Problem.PROBLEM.UPDATE_TIME;
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
    public String component3() {
        return getContent();
    }

    @Override
    public Boolean component4() {
        return getSpj();
    }

    @Override
    public LocalDateTime component5() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime component6() {
        return getUpdateTime();
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
    public String value3() {
        return getContent();
    }

    @Override
    public Boolean value4() {
        return getSpj();
    }

    @Override
    public LocalDateTime value5() {
        return getCreateTime();
    }

    @Override
    public LocalDateTime value6() {
        return getUpdateTime();
    }

    @Override
    public ProblemRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public ProblemRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public ProblemRecord value3(String value) {
        setContent(value);
        return this;
    }

    @Override
    public ProblemRecord value4(Boolean value) {
        setSpj(value);
        return this;
    }

    @Override
    public ProblemRecord value5(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public ProblemRecord value6(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public ProblemRecord values(Long value1, String value2, String value3, Boolean value4, LocalDateTime value5, LocalDateTime value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ProblemRecord
     */
    public ProblemRecord() {
        super(Problem.PROBLEM);
    }

    /**
     * Create a detached, initialised ProblemRecord
     */
    public ProblemRecord(Long id, String name, String content, Boolean spj, LocalDateTime createTime, LocalDateTime updateTime) {
        super(Problem.PROBLEM);

        setId(id);
        setName(name);
        setContent(content);
        setSpj(spj);
        setCreateTime(createTime);
        setUpdateTime(updateTime);
    }

    /**
     * Create a detached, initialised ProblemRecord
     */
    public ProblemRecord(com.kairlec.koj.dao.tables.pojos.Problem value) {
        super(Problem.PROBLEM);

        if (value != null) {
            setId(value.getId());
            setName(value.getName());
            setContent(value.getContent());
            setSpj(value.getSpj());
            setCreateTime(value.getCreateTime());
            setUpdateTime(value.getUpdateTime());
        }
    }
}
