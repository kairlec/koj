/*
 * This file is generated by jOOQ.
 */
package com.kairlec.koj.dao;


import com.kairlec.koj.dao.tables.Competition;
import com.kairlec.koj.dao.tables.Contestants;
import com.kairlec.koj.dao.tables.Problem;
import com.kairlec.koj.dao.tables.ProblemBelongCompetition;
import com.kairlec.koj.dao.tables.ProblemConfig;
import com.kairlec.koj.dao.tables.ProblemRun;
import com.kairlec.koj.dao.tables.ProblemTag;
import com.kairlec.koj.dao.tables.Submit;
import com.kairlec.koj.dao.tables.SubmitExtend;
import com.kairlec.koj.dao.tables.TagBelongProblem;
import com.kairlec.koj.dao.tables.UidWorkerNode;
import com.kairlec.koj.dao.tables.User;
import com.kairlec.koj.dao.tables.records.CompetitionRecord;
import com.kairlec.koj.dao.tables.records.ContestantsRecord;
import com.kairlec.koj.dao.tables.records.ProblemBelongCompetitionRecord;
import com.kairlec.koj.dao.tables.records.ProblemConfigRecord;
import com.kairlec.koj.dao.tables.records.ProblemRecord;
import com.kairlec.koj.dao.tables.records.ProblemRunRecord;
import com.kairlec.koj.dao.tables.records.ProblemTagRecord;
import com.kairlec.koj.dao.tables.records.SubmitExtendRecord;
import com.kairlec.koj.dao.tables.records.SubmitRecord;
import com.kairlec.koj.dao.tables.records.TagBelongProblemRecord;
import com.kairlec.koj.dao.tables.records.UidWorkerNodeRecord;
import com.kairlec.koj.dao.tables.records.UserRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in koj.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<CompetitionRecord> KEY_COMPETITION_PRIMARY = Internal.createUniqueKey(Competition.COMPETITION, DSL.name("KEY_competition_PRIMARY"), new TableField[] { Competition.COMPETITION.ID }, true);
    public static final UniqueKey<ContestantsRecord> KEY_CONTESTANTS_PRIMARY = Internal.createUniqueKey(Contestants.CONTESTANTS, DSL.name("KEY_contestants_PRIMARY"), new TableField[] { Contestants.CONTESTANTS.USER_ID, Contestants.CONTESTANTS.COMPETITION_ID }, true);
    public static final UniqueKey<ProblemRecord> KEY_PROBLEM_PRIMARY = Internal.createUniqueKey(Problem.PROBLEM, DSL.name("KEY_problem_PRIMARY"), new TableField[] { Problem.PROBLEM.ID }, true);
    public static final UniqueKey<ProblemBelongCompetitionRecord> KEY_PROBLEM_BELONG_COMPETITION_PRIMARY = Internal.createUniqueKey(ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION, DSL.name("KEY_problem_belong_competition_PRIMARY"), new TableField[] { ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION.PROBLEM_ID, ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION.COMPETITION_ID }, true);
    public static final UniqueKey<ProblemConfigRecord> KEY_PROBLEM_CONFIG_PRIMARY = Internal.createUniqueKey(ProblemConfig.PROBLEM_CONFIG, DSL.name("KEY_problem_config_PRIMARY"), new TableField[] { ProblemConfig.PROBLEM_CONFIG.PROBLEM_ID, ProblemConfig.PROBLEM_CONFIG.LANGUAGE_ID }, true);
    public static final UniqueKey<ProblemRunRecord> KEY_PROBLEM_RUN_PRIMARY = Internal.createUniqueKey(ProblemRun.PROBLEM_RUN, DSL.name("KEY_problem_run_PRIMARY"), new TableField[] { ProblemRun.PROBLEM_RUN.ID }, true);
    public static final UniqueKey<ProblemTagRecord> KEY_PROBLEM_TAG_NAME_UQ = Internal.createUniqueKey(ProblemTag.PROBLEM_TAG, DSL.name("KEY_problem_tag_name_uq"), new TableField[] { ProblemTag.PROBLEM_TAG.NAME }, true);
    public static final UniqueKey<ProblemTagRecord> KEY_PROBLEM_TAG_PRIMARY = Internal.createUniqueKey(ProblemTag.PROBLEM_TAG, DSL.name("KEY_problem_tag_PRIMARY"), new TableField[] { ProblemTag.PROBLEM_TAG.ID }, true);
    public static final UniqueKey<SubmitRecord> KEY_SUBMIT_PRIMARY = Internal.createUniqueKey(Submit.SUBMIT, DSL.name("KEY_submit_PRIMARY"), new TableField[] { Submit.SUBMIT.ID }, true);
    public static final UniqueKey<SubmitExtendRecord> KEY_SUBMIT_EXTEND_PRIMARY = Internal.createUniqueKey(SubmitExtend.SUBMIT_EXTEND, DSL.name("KEY_submit_extend_PRIMARY"), new TableField[] { SubmitExtend.SUBMIT_EXTEND.ID }, true);
    public static final UniqueKey<TagBelongProblemRecord> KEY_TAG_BELONG_PROBLEM_PRIMARY = Internal.createUniqueKey(TagBelongProblem.TAG_BELONG_PROBLEM, DSL.name("KEY_tag_belong_problem_PRIMARY"), new TableField[] { TagBelongProblem.TAG_BELONG_PROBLEM.PROBLEM_ID, TagBelongProblem.TAG_BELONG_PROBLEM.TAG_ID }, true);
    public static final UniqueKey<UidWorkerNodeRecord> KEY_UID_WORKER_NODE_PRIMARY = Internal.createUniqueKey(UidWorkerNode.UID_WORKER_NODE, DSL.name("KEY_uid_worker_node_PRIMARY"), new TableField[] { UidWorkerNode.UID_WORKER_NODE.ID }, true);
    public static final UniqueKey<UserRecord> KEY_USER_EMAIL_UQ = Internal.createUniqueKey(User.USER, DSL.name("KEY_user_email_uq"), new TableField[] { User.USER.EMAIL }, true);
    public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = Internal.createUniqueKey(User.USER, DSL.name("KEY_user_PRIMARY"), new TableField[] { User.USER.ID }, true);
    public static final UniqueKey<UserRecord> KEY_USER_USERNAME_UQ = Internal.createUniqueKey(User.USER, DSL.name("KEY_user_username_uq"), new TableField[] { User.USER.USERNAME }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<ContestantsRecord, CompetitionRecord> COMPETITION_ID_FK = Internal.createForeignKey(Contestants.CONTESTANTS, DSL.name("competition_id_fk"), new TableField[] { Contestants.CONTESTANTS.COMPETITION_ID }, Keys.KEY_COMPETITION_PRIMARY, new TableField[] { Competition.COMPETITION.ID }, true);
    public static final ForeignKey<ContestantsRecord, UserRecord> USER_ID_FK = Internal.createForeignKey(Contestants.CONTESTANTS, DSL.name("user_id_fk"), new TableField[] { Contestants.CONTESTANTS.USER_ID }, Keys.KEY_USER_PRIMARY, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<ProblemBelongCompetitionRecord, ProblemRecord> PROBLEM_BELONG_COMPETITION_IBFK_1 = Internal.createForeignKey(ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION, DSL.name("problem_belong_competition_ibfk_1"), new TableField[] { ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION.PROBLEM_ID }, Keys.KEY_PROBLEM_PRIMARY, new TableField[] { Problem.PROBLEM.ID }, true);
    public static final ForeignKey<ProblemBelongCompetitionRecord, CompetitionRecord> PROBLEM_BELONG_COMPETITION_IBFK_2 = Internal.createForeignKey(ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION, DSL.name("problem_belong_competition_ibfk_2"), new TableField[] { ProblemBelongCompetition.PROBLEM_BELONG_COMPETITION.COMPETITION_ID }, Keys.KEY_COMPETITION_PRIMARY, new TableField[] { Competition.COMPETITION.ID }, true);
    public static final ForeignKey<ProblemConfigRecord, ProblemRecord> CONFIG_PROBLEM_ID_FK = Internal.createForeignKey(ProblemConfig.PROBLEM_CONFIG, DSL.name("config_problem_id_fk"), new TableField[] { ProblemConfig.PROBLEM_CONFIG.PROBLEM_ID }, Keys.KEY_PROBLEM_PRIMARY, new TableField[] { Problem.PROBLEM.ID }, true);
    public static final ForeignKey<ProblemRunRecord, ProblemRecord> PROBLEM_RUN_FK = Internal.createForeignKey(ProblemRun.PROBLEM_RUN, DSL.name("problem_run_FK"), new TableField[] { ProblemRun.PROBLEM_RUN.ID }, Keys.KEY_PROBLEM_PRIMARY, new TableField[] { Problem.PROBLEM.ID }, true);
    public static final ForeignKey<SubmitRecord, CompetitionRecord> SUBMIT_IBFK_1 = Internal.createForeignKey(Submit.SUBMIT, DSL.name("submit_ibfk_1"), new TableField[] { Submit.SUBMIT.BELONG_COMPETITION_ID }, Keys.KEY_COMPETITION_PRIMARY, new TableField[] { Competition.COMPETITION.ID }, true);
    public static final ForeignKey<SubmitRecord, UserRecord> SUBMIT_IBFK_2 = Internal.createForeignKey(Submit.SUBMIT, DSL.name("submit_ibfk_2"), new TableField[] { Submit.SUBMIT.BELONG_USER_ID }, Keys.KEY_USER_PRIMARY, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<SubmitRecord, ProblemRecord> SUBMIT_IBFK_3 = Internal.createForeignKey(Submit.SUBMIT, DSL.name("submit_ibfk_3"), new TableField[] { Submit.SUBMIT.PROBLEM_ID }, Keys.KEY_PROBLEM_PRIMARY, new TableField[] { Problem.PROBLEM.ID }, true);
    public static final ForeignKey<SubmitExtendRecord, SubmitRecord> SUBMIT_EXTEND_IBFK_1 = Internal.createForeignKey(SubmitExtend.SUBMIT_EXTEND, DSL.name("submit_extend_ibfk_1"), new TableField[] { SubmitExtend.SUBMIT_EXTEND.ID }, Keys.KEY_SUBMIT_PRIMARY, new TableField[] { Submit.SUBMIT.ID }, true);
    public static final ForeignKey<TagBelongProblemRecord, ProblemRecord> PROBLEM_ID_FK = Internal.createForeignKey(TagBelongProblem.TAG_BELONG_PROBLEM, DSL.name("problem_id_fk"), new TableField[] { TagBelongProblem.TAG_BELONG_PROBLEM.PROBLEM_ID }, Keys.KEY_PROBLEM_PRIMARY, new TableField[] { Problem.PROBLEM.ID }, true);
    public static final ForeignKey<TagBelongProblemRecord, ProblemTagRecord> TAG_ID_FK = Internal.createForeignKey(TagBelongProblem.TAG_BELONG_PROBLEM, DSL.name("tag_id_fk"), new TableField[] { TagBelongProblem.TAG_BELONG_PROBLEM.TAG_ID }, Keys.KEY_PROBLEM_TAG_PRIMARY, new TableField[] { ProblemTag.PROBLEM_TAG.ID }, true);
}
