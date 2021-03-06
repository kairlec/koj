/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.records;


import com.kairlec.koj.dao.tables.UidWorkerNode;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * DB WorkerID Assigner for UID Generator
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UidWorkerNodeRecord extends UpdatableRecordImpl<UidWorkerNodeRecord> implements Record7<Long, String, String, Integer, LocalDate, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>koj.uid_worker_node.id</code>. auto increment id
     */
    public UidWorkerNodeRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.id</code>. auto increment id
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>koj.uid_worker_node.host_name</code>. host name
     */
    public UidWorkerNodeRecord setHostName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.host_name</code>. host name
     */
    public String getHostName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>koj.uid_worker_node.port</code>. port
     */
    public UidWorkerNodeRecord setPort(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.port</code>. port
     */
    public String getPort() {
        return (String) get(2);
    }

    /**
     * Setter for <code>koj.uid_worker_node.type</code>. node type: ACTUAL or
     * CONTAINER
     */
    public UidWorkerNodeRecord setType(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.type</code>. node type: ACTUAL or
     * CONTAINER
     */
    public Integer getType() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>koj.uid_worker_node.launch_date</code>. launch date
     */
    public UidWorkerNodeRecord setLaunchDate(LocalDate value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.launch_date</code>. launch date
     */
    public LocalDate getLaunchDate() {
        return (LocalDate) get(4);
    }

    /**
     * Setter for <code>koj.uid_worker_node.update_time</code>. modified time
     */
    public UidWorkerNodeRecord setUpdateTime(LocalDateTime value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.update_time</code>. modified time
     */
    public LocalDateTime getUpdateTime() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>koj.uid_worker_node.create_time</code>. created time
     */
    public UidWorkerNodeRecord setCreateTime(LocalDateTime value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>koj.uid_worker_node.create_time</code>. created time
     */
    public LocalDateTime getCreateTime() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, String, String, Integer, LocalDate, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, String, String, Integer, LocalDate, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return UidWorkerNode.UID_WORKER_NODE.ID;
    }

    @Override
    public Field<String> field2() {
        return UidWorkerNode.UID_WORKER_NODE.HOST_NAME;
    }

    @Override
    public Field<String> field3() {
        return UidWorkerNode.UID_WORKER_NODE.PORT;
    }

    @Override
    public Field<Integer> field4() {
        return UidWorkerNode.UID_WORKER_NODE.TYPE;
    }

    @Override
    public Field<LocalDate> field5() {
        return UidWorkerNode.UID_WORKER_NODE.LAUNCH_DATE;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return UidWorkerNode.UID_WORKER_NODE.UPDATE_TIME;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return UidWorkerNode.UID_WORKER_NODE.CREATE_TIME;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getHostName();
    }

    @Override
    public String component3() {
        return getPort();
    }

    @Override
    public Integer component4() {
        return getType();
    }

    @Override
    public LocalDate component5() {
        return getLaunchDate();
    }

    @Override
    public LocalDateTime component6() {
        return getUpdateTime();
    }

    @Override
    public LocalDateTime component7() {
        return getCreateTime();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getHostName();
    }

    @Override
    public String value3() {
        return getPort();
    }

    @Override
    public Integer value4() {
        return getType();
    }

    @Override
    public LocalDate value5() {
        return getLaunchDate();
    }

    @Override
    public LocalDateTime value6() {
        return getUpdateTime();
    }

    @Override
    public LocalDateTime value7() {
        return getCreateTime();
    }

    @Override
    public UidWorkerNodeRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord value2(String value) {
        setHostName(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord value3(String value) {
        setPort(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord value4(Integer value) {
        setType(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord value5(LocalDate value) {
        setLaunchDate(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord value6(LocalDateTime value) {
        setUpdateTime(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord value7(LocalDateTime value) {
        setCreateTime(value);
        return this;
    }

    @Override
    public UidWorkerNodeRecord values(Long value1, String value2, String value3, Integer value4, LocalDate value5, LocalDateTime value6, LocalDateTime value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UidWorkerNodeRecord
     */
    public UidWorkerNodeRecord() {
        super(UidWorkerNode.UID_WORKER_NODE);
    }

    /**
     * Create a detached, initialised UidWorkerNodeRecord
     */
    public UidWorkerNodeRecord(Long id, String hostName, String port, Integer type, LocalDate launchDate, LocalDateTime updateTime, LocalDateTime createTime) {
        super(UidWorkerNode.UID_WORKER_NODE);

        setId(id);
        setHostName(hostName);
        setPort(port);
        setType(type);
        setLaunchDate(launchDate);
        setUpdateTime(updateTime);
        setCreateTime(createTime);
    }

    /**
     * Create a detached, initialised UidWorkerNodeRecord
     */
    public UidWorkerNodeRecord(com.kairlec.koj.dao.tables.pojos.UidWorkerNode value) {
        super(UidWorkerNode.UID_WORKER_NODE);

        if (value != null) {
            setId(value.getId());
            setHostName(value.getHostName());
            setPort(value.getPort());
            setType(value.getType());
            setLaunchDate(value.getLaunchDate());
            setUpdateTime(value.getUpdateTime());
            setCreateTime(value.getCreateTime());
        }
    }
}
