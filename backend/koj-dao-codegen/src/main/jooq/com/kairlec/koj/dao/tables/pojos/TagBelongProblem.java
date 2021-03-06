/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 题目标签关系表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TagBelongProblem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long          problemId;
    private final Long          tagId;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;

    public TagBelongProblem(TagBelongProblem value) {
        this.problemId = value.problemId;
        this.tagId = value.tagId;
        this.createTime = value.createTime;
        this.updateTime = value.updateTime;
    }

    public TagBelongProblem(
        Long          problemId,
        Long          tagId,
        LocalDateTime createTime,
        LocalDateTime updateTime
    ) {
        this.problemId = problemId;
        this.tagId = tagId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * Getter for <code>koj.tag_belong_problem.problem_id</code>. 题目id
     */
    public Long getProblemId() {
        return this.problemId;
    }

    /**
     * Getter for <code>koj.tag_belong_problem.tag_id</code>. 标签id
     */
    public Long getTagId() {
        return this.tagId;
    }

    /**
     * Getter for <code>koj.tag_belong_problem.create_time</code>. 创建时间
     */
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    /**
     * Getter for <code>koj.tag_belong_problem.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TagBelongProblem (");

        sb.append(problemId);
        sb.append(", ").append(tagId);
        sb.append(", ").append(createTime);
        sb.append(", ").append(updateTime);

        sb.append(")");
        return sb.toString();
    }
}
