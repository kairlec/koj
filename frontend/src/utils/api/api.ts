import { KOJAxiosInstance, KOJAxiosRequestConfig, KOJAxiosResponse } from '~/http'
import {
  ListCondition,
  ManageCompetition,
  ManageCompetitionCreateRequest,
  PageData,
  ProblemConfigManage,
  ProblemDetail,
  SimpleCompetition,
  SimpleProblem,
  SimpleSubmit,
  SubmitDetail,
  SubmitRequest,
  Tag,
  User,
  UserManageDetail,
  UserRankInfo,
  UserStat,
  UserType,
} from '~/apiDeclaration'
import { AxiosResponse } from 'axios'

export interface UserApi {
  registerUser(username: string, password: string, email: string, config?: KOJAxiosRequestConfig): Promise<string>
  loginUser(usernameOrEmail: string, password: string, config?: KOJAxiosRequestConfig): Promise<User>
  destroy(config?: KOJAxiosRequestConfig): Promise<undefined>
  stat(username: string, config?: KOJAxiosRequestConfig): Promise<UserStat>
  existsUsernameOrEmail(usernameOrEmail: string, config?: KOJAxiosRequestConfig): Promise<boolean>
  self(config?: KOJAxiosRequestConfig): Promise<User>
  forgetPassword(username: string, email: string, config?: KOJAxiosRequestConfig): Promise<undefined>
  resetPassword(
    username: string,
    email: string,
    verifyCode: string,
    newPassword: string,
    config?: KOJAxiosRequestConfig,
  ): Promise<undefined>
  userList(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<UserManageDetail>>
  manageUpdateUser(
    userId: string,
    data: { username?: string; email?: string; password?: string; type?: UserType; blocked?: boolean },
    config?: KOJAxiosRequestConfig,
  ): Promise<AxiosResponse>
  rank(limit?: number, config?: KOJAxiosRequestConfig): Promise<UserRankInfo[]>
}

export interface ProblemApi {
  problems(tags?: string[], listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleProblem>>
  problem(id: string, config?: KOJAxiosRequestConfig): Promise<ProblemDetail>
  updateProblem(
    id: string,
    data: { name?: string; content?: string; spj?: boolean; tags?: string[] },
    config?: KOJAxiosRequestConfig,
  ): Promise<void>
  tags(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<Tag>>
  addProblemTag(problemId: string, tagId: string, config?: KOJAxiosRequestConfig): Promise<void>
  deleteProblem(problemId: string, config?: KOJAxiosRequestConfig): Promise<void>
  deleteProblemTag(problemId: string, tagId: string, config?: KOJAxiosRequestConfig): Promise<void>
  updateTag(id: string, name: string, config?: KOJAxiosRequestConfig): Promise<void>
  addProblem(problem: { name: string; content: string; spj: boolean; tags: string[] }, config?: KOJAxiosRequestConfig): Promise<string>
  addConfig(
    problemId: string,
    data: {
      languageId: string
      time: number
      memory: number
      maxOutputSize?: string
      maxStack?: string
      maxProcessNumber?: number
      args?: string[]
      env?: string[]
    },
    config?: KOJAxiosRequestConfig,
  ): Promise<void>
  getConfigs(problemId: string, config?: KOJAxiosRequestConfig): Promise<ProblemConfigManage[]>
  deleteConfig(problemId: string, languageId: string, config?: KOJAxiosRequestConfig): Promise<void>
  saveRunConfig(problemId: string, data: { stdin: string; ansout: string }, config?: KOJAxiosRequestConfig): Promise<void>
  getRunConfig(problemId: string, config?: KOJAxiosRequestConfig): Promise<{ stdin: string; ansout: string }>
}

export interface SubmitApi {
  submits(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleSubmit>>
  submit(submitRequest: SubmitRequest, config?: KOJAxiosRequestConfig): Promise<void>
  submitDetail(id: string, config?: KOJAxiosRequestConfig): Promise<SubmitDetail>
  languages(config?: KOJAxiosRequestConfig): Promise<string[]>
}

export interface CompetitionApi {
  getCompetition(id: string, config?: KOJAxiosRequestConfig): Promise<ManageCompetition>
  getCompetitionList(listCondition: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleCompetition>>
  joinCompetition(id: string, pwd?: string, config?: KOJAxiosRequestConfig): Promise<void>
  createCompetition(request: ManageCompetitionCreateRequest, config?: KOJAxiosRequestConfig): Promise<string>
  updateCompetition(id: string, data: { name?: string; pwd?: string }, config?: KOJAxiosRequestConfig): Promise<void>
  deleteCompetition(id: string, config?: KOJAxiosRequestConfig): Promise<void>
  competitionProblems(id: string, config?: KOJAxiosRequestConfig): Promise<SimpleProblem[]>
  competitionSubmits(id: string, config?: KOJAxiosRequestConfig): Promise<SimpleSubmit[]>
  addCompetitionProblem(id: string, problemId: string, config?: KOJAxiosRequestConfig): Promise<KOJAxiosResponse>
  deleteCompetitionProblem(id: string, problemId: string, config?: KOJAxiosRequestConfig): Promise<void>
}

export interface IApi extends SubmitApi, ProblemApi, UserApi, CompetitionApi {
  withConfig(config: KOJAxiosRequestConfig): IApi

  axios: KOJAxiosInstance
}
