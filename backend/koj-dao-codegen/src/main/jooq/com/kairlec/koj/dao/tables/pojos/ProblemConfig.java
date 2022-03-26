/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 题目语言配置表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProblemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long          problemId;
    private final Long          languageId;
    private final Integer       memory;
    private final Integer       time;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;

    public ProblemConfig(ProblemConfig value) {
        this.problemId = value.problemId;
        this.languageId = value.languageId;
        this.memory = value.memory;
        this.time = value.time;
        this.createTime = value.createTime;
        this.updateTime = value.updateTime;
    }

    public ProblemConfig(
        Long          problemId,
        Long          languageId,
        Integer       memory,
        Integer       time,
        LocalDateTime createTime,
        LocalDateTime updateTime
    ) {
        this.problemId = problemId;
        this.languageId = languageId;
        this.memory = memory;
        this.time = time;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * Getter for <code>koj.problem_config.problem_id</code>. 题目id
     */
    public Long getProblemId() {
        return this.problemId;
    }

    /**
     * Getter for <code>koj.problem_config.language_id</code>. 语言id
     */
    public Long getLanguageId() {
        return this.languageId;
    }

    /**
     * Getter for <code>koj.problem_config.memory</code>. 内存限制
     */
    public Integer getMemory() {
        return this.memory;
    }

    /**
     * Getter for <code>koj.problem_config.time</code>. 时间限制
     */
    public Integer getTime() {
        return this.time;
    }

    /**
     * Getter for <code>koj.problem_config.create_time</code>. 创建时间
     */
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    /**
     * Getter for <code>koj.problem_config.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ProblemConfig (");

        sb.append(problemId);
        sb.append(", ").append(languageId);
        sb.append(", ").append(memory);
        sb.append(", ").append(time);
        sb.append(", ").append(createTime);
        sb.append(", ").append(updateTime);

        sb.append(")");
        return sb.toString();
    }
}
