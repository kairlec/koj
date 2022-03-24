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
