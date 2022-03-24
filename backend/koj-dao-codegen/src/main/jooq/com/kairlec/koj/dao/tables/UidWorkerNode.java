/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables;


import com.kairlec.koj.dao.Keys;
import com.kairlec.koj.dao.Koj;
import com.kairlec.koj.dao.tables.records.UidWorkerNodeRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * DB WorkerID Assigner for UID Generator
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UidWorkerNode extends TableImpl<UidWorkerNodeRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>koj.uid_worker_node</code>
     */
    public static final UidWorkerNode UID_WORKER_NODE = new UidWorkerNode();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UidWorkerNodeRecord> getRecordType() {
        return UidWorkerNodeRecord.class;
    }

    /**
     * The column <code>koj.uid_worker_node.id</code>. auto increment id
     */
    public final TableField<UidWorkerNodeRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "auto increment id");

    /**
     * The column <code>koj.uid_worker_node.host_name</code>. host name
     */
    public final TableField<UidWorkerNodeRecord, String> HOST_NAME = createField(DSL.name("host_name"), SQLDataType.VARCHAR(64).nullable(false), this, "host name");

    /**
     * The column <code>koj.uid_worker_node.port</code>. port
     */
    public final TableField<UidWorkerNodeRecord, String> PORT = createField(DSL.name("port"), SQLDataType.VARCHAR(64).nullable(false), this, "port");

    /**
     * The column <code>koj.uid_worker_node.type</code>. node type: ACTUAL or CONTAINER
     */
    public final TableField<UidWorkerNodeRecord, Integer> TYPE = createField(DSL.name("type"), SQLDataType.INTEGER.nullable(false), this, "node type: ACTUAL or CONTAINER");

    /**
     * The column <code>koj.uid_worker_node.launch_date</code>. launch date
     */
    public final TableField<UidWorkerNodeRecord, LocalDate> LAUNCH_DATE = createField(DSL.name("launch_date"), SQLDataType.LOCALDATE.nullable(false), this, "launch date");

    /**
     * The column <code>koj.uid_worker_node.update_time</code>. modified time
     */
    public final TableField<UidWorkerNodeRecord, LocalDateTime> UPDATE_TIME = createField(DSL.name("update_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "modified time");

    /**
     * The column <code>koj.uid_worker_node.create_time</code>. created time
     */
    public final TableField<UidWorkerNodeRecord, LocalDateTime> CREATE_TIME = createField(DSL.name("create_time"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "created time");

    private UidWorkerNode(Name alias, Table<UidWorkerNodeRecord> aliased) {
        this(alias, aliased, null);
    }

    private UidWorkerNode(Name alias, Table<UidWorkerNodeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("DB WorkerID Assigner for UID Generator"), TableOptions.table());
    }

    /**
     * Create an aliased <code>koj.uid_worker_node</code> table reference
     */
    public UidWorkerNode(String alias) {
        this(DSL.name(alias), UID_WORKER_NODE);
    }

    /**
     * Create an aliased <code>koj.uid_worker_node</code> table reference
     */
    public UidWorkerNode(Name alias) {
        this(alias, UID_WORKER_NODE);
    }

    /**
     * Create a <code>koj.uid_worker_node</code> table reference
     */
    public UidWorkerNode() {
        this(DSL.name("uid_worker_node"), null);
    }

    public <O extends Record> UidWorkerNode(Table<O> child, ForeignKey<O, UidWorkerNodeRecord> key) {
        super(child, key, UID_WORKER_NODE);
    }

    @Override
    public Schema getSchema() {
        return Koj.KOJ;
    }

    @Override
    public Identity<UidWorkerNodeRecord, Long> getIdentity() {
        return (Identity<UidWorkerNodeRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<UidWorkerNodeRecord> getPrimaryKey() {
        return Keys.KEY_UID_WORKER_NODE_PRIMARY;
    }

    @Override
    public List<UniqueKey<UidWorkerNodeRecord>> getKeys() {
        return Arrays.<UniqueKey<UidWorkerNodeRecord>>asList(Keys.KEY_UID_WORKER_NODE_PRIMARY);
    }

    @Override
    public UidWorkerNode as(String alias) {
        return new UidWorkerNode(DSL.name(alias), this);
    }

    @Override
    public UidWorkerNode as(Name alias) {
        return new UidWorkerNode(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UidWorkerNode rename(String name) {
        return new UidWorkerNode(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UidWorkerNode rename(Name name) {
        return new UidWorkerNode(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, String, String, Integer, LocalDate, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}