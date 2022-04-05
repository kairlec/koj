/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 题目和比赛关系表
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProblemBelongCompetition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Byte          idx;
    private final Long          problemId;
    private final Long          competitionId;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;

    public ProblemBelongCompetition(ProblemBelongCompetition value) {
        this.idx = value.idx;
        this.problemId = value.problemId;
        this.competitionId = value.competitionId;
        this.createTime = value.createTime;
        this.updateTime = value.updateTime;
    }

    public ProblemBelongCompetition(
        Byte          idx,
        Long          problemId,
        Long          competitionId,
        LocalDateTime createTime,
        LocalDateTime updateTime
    ) {
        this.idx = idx;
        this.problemId = problemId;
        this.competitionId = competitionId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * Getter for <code>koj.problem_belong_competition.idx</code>. 题目在比赛中的序号
     */
    public Byte getIdx() {
        return this.idx;
    }

    /**
     * Getter for <code>koj.problem_belong_competition.problem_id</code>. 题目id
     */
    public Long getProblemId() {
        return this.problemId;
    }

    /**
     * Getter for <code>koj.problem_belong_competition.competition_id</code>.
     * 比赛id
     */
    public Long getCompetitionId() {
        return this.competitionId;
    }

    /**
     * Getter for <code>koj.problem_belong_competition.create_time</code>. 创建时间
     */
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    /**
     * Getter for <code>koj.problem_belong_competition.update_time</code>. 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ProblemBelongCompetition (");

        sb.append(idx);
        sb.append(", ").append(problemId);
        sb.append(", ").append(competitionId);
        sb.append(", ").append(createTime);
        sb.append(", ").append(updateTime);

        sb.append(")");
        return sb.toString();
    }
}
