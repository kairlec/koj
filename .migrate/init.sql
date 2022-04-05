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
    id          BIGINT                                 NOT NULL AUTO_INCREMENT COMMENT '用户id',
    username    VARCHAR(64)                            NOT NULL COMMENT '用户名',
    password    CHAR(64)                               NOT NULL COMMENT '密码(脱敏后)',
    email       VARCHAR(32)                            NOT NULL COMMENT '用户邮箱',
    type        TINYINT                                NOT NULL COMMENT '用户类型: 0-普通用户, 1-管理员',
    create_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX username_idx (username),
    INDEX email_idx (email),
    CONSTRAINT email_uq UNIQUE (email),
    CONSTRAINT username_uq UNIQUE (username)
) COMMENT ='用户表';

CREATE TABLE IF NOT EXISTS koj.competition
(
    id          BIGINT                 NOT NULL AUTO_INCREMENT COMMENT '比赛id',
    name        VARCHAR(64)            NOT NULL COMMENT '比赛名称',
    pwd         CHAR(64)               NOT NULL COMMENT '比赛密码(脱敏后)',
    start_time  DATETIME               NOT NULL COMMENT '比赛开始时间',
    end_time    DATETIME               NOT NULL COMMENT '比赛结束时间',
    create_time DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX name_idx (name)
) COMMENT ='比赛表';

CREATE TABLE IF NOT EXISTS koj.submit
(
    id                    BIGINT                                 NOT NULL COMMENT '任务id',
    state                 TINYINT                                NOT NULL COMMENT '任务状态',
    language_id           VARCHAR(64)                            NOT NULL COMMENT '语言id',
    cast_memory           INT                                    NULL COMMENT '任务内存',
    cast_time             INT                                    NULL COMMENT '耗时',
    belong_competition_id BIGINT                                 NULL COMMENT '所属比赛id',
    belong_user_id        BIGINT                                 NOT NULL COMMENT '所属用户id',
    create_time           DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time           DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (belong_competition_id) REFERENCES koj.competition (id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (belong_user_id) REFERENCES koj.user (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT ='任务表';

CREATE TABLE IF NOT EXISTS koj.code
(
    id          BIGINT                 NOT NULL COMMENT '代码id(和任务id一致)',
    code        TEXT                   NOT NULL COMMENT '代码',
    create_time DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    CONSTRAINT id FOREIGN KEY (id) REFERENCES koj.submit (id) ON DELETE CASCADE
) COMMENT ='代码表';

CREATE TABLE IF NOT EXISTS koj.contestants
(
    user_id        BIGINT                                 NOT NULL COMMENT '用户id',
    competition_id BIGINT                                 NOT NULL COMMENT '比赛id',
    create_time    DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time    DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (user_id, competition_id),
    INDEX user_id_idx (user_id),
    INDEX competition_id_idx (competition_id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES koj.user (id) ON DELETE CASCADE,
    CONSTRAINT competition_id_fk FOREIGN KEY (competition_id) REFERENCES koj.competition (id) ON DELETE CASCADE
) COMMENT ='参赛者关系表';

CREATE TABLE IF NOT EXISTS koj.problem_tag
(
    id          BIGINT                                 NOT NULL AUTO_INCREMENT COMMENT '标签id',
    name        VARCHAR(64)                            NOT NULL COMMENT '标签名',
    create_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX name_idx (name)
) COMMENT ='题目标签表';

CREATE TABLE IF NOT EXISTS koj.problem
(
    id          BIGINT                 NOT NULL AUTO_INCREMENT COMMENT '题目id',
    name        VARCHAR(64)            NOT NULL COMMENT '题目名称',
    content     TEXT                   NOT NULL COMMENT '题目内容(压缩过)',
    spj         BOOLEAN  DEFAULT FALSE NOT NULL COMMENT '是否为spj',
    create_time DATETIME DEFAULT NOW() NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX name_idx (name)
) COMMENT ='题目表';

CREATE TABLE IF NOT EXISTS koj.problem_belong_competition
(
    idx            TINYINT                                NOT NULL COMMENT '题目在比赛中的序号',
    problem_id     BIGINT                                 NOT NULL COMMENT '题目id',
    competition_id BIGINT                                 NOT NULL COMMENT '比赛id',
    PRIMARY KEY (problem_id, competition_id),
    create_time    DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time    DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    INDEX problem_id_idx (problem_id),
    INDEX competition_id_idx (competition_id),
    FOREIGN KEY (problem_id) REFERENCES koj.problem (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (competition_id) REFERENCES koj.competition (id) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT ='题目和比赛关系表';

CREATE TABLE IF NOT EXISTS koj.tag_belong_problem
(
    problem_id  BIGINT                                 NOT NULL COMMENT '题目id',
    tag_id      BIGINT                                 NOT NULL COMMENT '标签id',
    create_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (problem_id, tag_id),
    INDEX problem_id_idx (problem_id),
    INDEX tag_id_idx (tag_id),
    CONSTRAINT problem_id_fk FOREIGN KEY (problem_id) REFERENCES koj.problem (id) ON DELETE CASCADE,
    CONSTRAINT tag_id_fk FOREIGN KEY (tag_id) REFERENCES koj.problem_tag (id) ON DELETE CASCADE
) COMMENT ='题目标签关系表';

CREATE TABLE IF NOT EXISTS koj.problem_config
(
    problem_id  BIGINT                                 NOT NULL COMMENT '题目id',
    language_id VARCHAR(64)                            NOT NULL COMMENT '语言id',
#     spj_content TEXT                                   NULL COMMENT 'spj内容',
    memory      INT                                    NOT NULL COMMENT '内存限制',
    time        INT                                    NOT NULL COMMENT '时间限制',
    create_time DATETIME DEFAULT NOW()                 NOT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW() NOT NULL COMMENT '更新时间',
    PRIMARY KEY (problem_id, language_id),
    INDEX problem_id_idx (problem_id),
    INDEX language_id_idx (language_id),
    CONSTRAINT config_problem_id_fk FOREIGN KEY (problem_id) REFERENCES koj.problem (id) ON DELETE CASCADE
) COMMENT ='题目语言配置表';