/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;

import org.jooq.types.ULong;
import org.jooq.types.UShort;


/**
 * 题目语言配置表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProblemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long          problemId;
    private final String        languageId;
    private final Integer       memory;
    private final Integer       time;
    private final ULong         maxOutputSize;
    private final ULong         maxStack;
    private final UShort        maxProcessNumber;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;
    private final String        args;
    private final String        env;

    public ProblemConfig(ProblemConfig value) {
        this.problemId = value.problemId;
        this.languageId = value.languageId;
        this.memory = value.memory;
        this.time = value.time;
        this.maxOutputSize = value.maxOutputSize;
        this.maxStack = value.maxStack;
        this.maxProcessNumber = value.maxProcessNumber;
        this.createTime = value.createTime;
        this.updateTime = value.updateTime;
        this.args = value.args;
        this.env = value.env;
    }

    public ProblemConfig(
        Long          problemId,
        String        languageId,
        Integer       memory,
        Integer       time,
        ULong         maxOutputSize,
        ULong         maxStack,
        UShort        maxProcessNumber,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        String        args,
        String        env
    ) {
        this.problemId = problemId;
        this.languageId = languageId;
        this.memory = memory;
        this.time = time;
        this.maxOutputSize = maxOutputSize;
        this.maxStack = maxStack;
        this.maxProcessNumber = maxProcessNumber;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.args = args;
        this.env = env;
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
    public String getLanguageId() {
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
     * Getter for <code>koj.problem_config.max_output_size</code>.
     * 最大输出限制(字节),默认无限制
     */
    public ULong getMaxOutputSize() {
        return this.maxOutputSize;
    }

    /**
     * Getter for <code>koj.problem_config.max_stack</code>. 最大堆栈(字节),默认无限制
     */
    public ULong getMaxStack() {
        return this.maxStack;
    }

    /**
     * Getter for <code>koj.problem_config.max_process_number</code>.
     * 最大处理器数量,默认1
     */
    public UShort getMaxProcessNumber() {
        return this.maxProcessNumber;
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

    /**
     * Getter for <code>koj.problem_config.args</code>. 额外参数(默认无)
     */
    public String getArgs() {
        return this.args;
    }

    /**
     * Getter for <code>koj.problem_config.env</code>. 额外环境参数(默认无)
     */
    public String getEnv() {
        return this.env;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ProblemConfig (");

        sb.append(problemId);
        sb.append(", ").append(languageId);
        sb.append(", ").append(memory);
        sb.append(", ").append(time);
        sb.append(", ").append(maxOutputSize);
        sb.append(", ").append(maxStack);
        sb.append(", ").append(maxProcessNumber);
        sb.append(", ").append(createTime);
        sb.append(", ").append(updateTime);
        sb.append(", ").append(args);
        sb.append(", ").append(env);

        sb.append(")");
        return sb.toString();
    }
}
