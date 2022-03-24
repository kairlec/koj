CREATE DATABASE IF NOT EXISTS koj CHARSET utf8mb4;

CREATE TABLE IF NOT EXISTS koj.uid_worker_node
(
    id          BIGINT                                 NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
    host_name   VARCHAR(64)                            NOT NULL COMMENT 'host name',
    port        VARCHAR(64)                            NOT NULL COMMENT 'port',
    type        INT                                    NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
    launch_date DATE                                   NOT NULL COMMENT 'launch date',
    update_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT 'modified time',
    create_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT 'created time',
    PRIMARY KEY (ID)
) COMMENT ='DB WorkerID Assigner for UID Generator';

CREATE TABLE IF NOT EXISTS koj.user
(
    id          BIGINT                                 NOT NULL COMMENT '用户id',
    username    VARCHAR(64)                            NOT NULL COMMENT '用户名',
    password    CHAR(64)                               NOT NULL COMMENT '密码(脱敏后)',
    email       VARCHAR(32)                            NOT NULL COMMENT '用户邮箱',
    create_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX username_idx (username),
    INDEX email_idx (email),
    CONSTRAINT email_uq UNIQUE (email),
    CONSTRAINT username_uq UNIQUE (username)
) COMMENT ='用户表';

CREATE TABLE IF NOT EXISTS koj.task
(
    id          BIGINT                                 NOT NULL COMMENT '任务id',
    type        BIGINT                                 NOT NULL COMMENT '任务类型',
    status      BIGINT                                 NOT NULL COMMENT '任务状态',
    create_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT ='任务表';
