import {
  ListCondition,
  listConditionAsParam,
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
  UserManageDetail,
  UserRankInfo,
  UserType,
} from '~/apiDeclaration'
import http, { KOJAxiosInstance, KOJAxiosRequestConfig, KOJAxiosResponse } from '~/http'
import { stringify } from 'qs'
import { KOJStorage } from '~/storage'
import { AxiosResponse } from 'axios'
import apiRoute from '~/api/requestRoute'
import { data, page, setTime } from '~/api/extra'
import { IApi } from '~/api/api'

export class ProblemDetailError extends Error {
  constructor(message: string, public problem: ProblemDetail) {
    super(message)
    this.name = 'ProblemDetailError'
  }
}

function createAPIInstance(axiosInstance: KOJAxiosInstance, addonConfig?: KOJAxiosRequestConfig): IApi {
  return {
    addCompetitionProblem(id: string, problemId: string, config?: KOJAxiosRequestConfig): Promise<KOJAxiosResponse> {
      return this.axios.put(apiRoute.admin.competitions.withProblem(id, problemId), null, { ...addonConfig, ...config })
    },
    competitionProblems(id: string, config?: KOJAxiosRequestConfig): Promise<SimpleProblem[]> {
      return data(this.axios.get(apiRoute.competitions.problems(id), { ...addonConfig, ...config }))
    },
    competitionSubmits(id: string, config?: KOJAxiosRequestConfig): Promise<SimpleSubmit[]> {
      return data(this.axios.get(apiRoute.competitions.submits(id), { ...addonConfig, ...config }))
    },
    deleteCompetitionProblem(id: string, problemId: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.delete(apiRoute.admin.competitions.withProblem(id, problemId), { ...addonConfig, ...config })
    },
    createCompetition(request: ManageCompetitionCreateRequest, config?: KOJAxiosRequestConfig): Promise<string> {
      return data(this.axios.put(apiRoute.admin.competitions.base(), request, { ...addonConfig, ...config }))
    },
    deleteCompetition(id: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.delete(apiRoute.admin.competitions.single(id), { ...addonConfig, ...config })
    },
    getCompetition(id: string, config?: KOJAxiosRequestConfig): Promise<ManageCompetition> {
      return data(this.axios.get(apiRoute.admin.competitions.single(id), { ...addonConfig, ...config }))
    },
    getCompetitionList(listCondition: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleCompetition>> {
      return page(
        this.axios.get(apiRoute.public.competitions.list(), {
          ...addonConfig,
          params: listConditionAsParam(listCondition),
          ...config,
        }),
      )
    },
    joinCompetition(id: string, pwd?: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.post(apiRoute.competitions.join(id), { ...addonConfig, params: { password: pwd }, ...config })
    },
    updateCompetition(id: string, data: { name?: string; pwd?: string }, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.patch(apiRoute.admin.competitions.single(id), data, { ...addonConfig, ...config })
    },
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
    ): Promise<void> {
      return this.axios.put(apiRoute.admin.problems.configs(problemId), data, {
        headers: {
          'content-type': 'application/json',
        },
        ...addonConfig,
        ...config,
      })
    },
    addProblem(problem: { name: string; content: string; spj: boolean; tags: string[] }, config?: KOJAxiosRequestConfig): Promise<string> {
      return data(
        this.axios.put(apiRoute.admin.problems.base(), problem, {
          headers: {
            'content-type': 'application/json',
          },
          ...addonConfig,
          ...config,
        }),
      )
    },
    addProblemTag(problemId: string, tagId: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.put(apiRoute.admin.problems.withTag(problemId, tagId), null, { ...addonConfig, ...config })
    },
    deleteConfig(problemId: string, languageId: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.delete(apiRoute.admin.problems.config(problemId, languageId), { ...addonConfig, ...config })
    },
    getConfigs(problemId: string, config?: KOJAxiosRequestConfig): Promise<ProblemConfigManage[]> {
      return data(this.axios.get(apiRoute.admin.problems.configList(problemId), { ...addonConfig, ...config }))
    },
    deleteProblem(problemId: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.delete(apiRoute.admin.problems.detail(problemId), { ...addonConfig, ...config })
    },
    deleteProblemTag(problemId: string, tagId: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.delete(apiRoute.admin.problems.withTag(problemId, tagId), { ...addonConfig, ...config })
    },
    getRunConfig(problemId: string, config?: KOJAxiosRequestConfig): Promise<{ stdin: string; ansout: string }> {
      return data(this.axios.get(apiRoute.admin.problems.runs(problemId), { ...addonConfig, ...config }))
    },
    saveRunConfig(problemId: string, data: { stdin: string; ansout: string }, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.put(apiRoute.admin.problems.runs(problemId), data, {
        headers: {
          'content-type': 'application/json',
        },
        ...addonConfig,
        ...config,
      })
    },
    updateTag(id: string, name: string, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.patch(apiRoute.admin.tags.base(), stringify({ name }), {
        ...addonConfig,
        ...config,
      })
    },
    withConfig(config: KOJAxiosRequestConfig): IApi {
      return createAPIInstance(axiosInstance, { ...addonConfig, ...config })
    },
    axios: axiosInstance,
    self(config?: KOJAxiosRequestConfig) {
      return data(this.axios.get(apiRoute.users.self(), { ignoreError: true, ...config }))
    },
    registerUser(username: string, password: string, email: string, config?: KOJAxiosRequestConfig) {
      return data(
        this.axios.put(
          apiRoute.public.users.register(),
          {
            username,
            password,
            email,
          },
          {
            ...addonConfig,
            ...config,
            headers: {
              'content-type': 'application/json',
            },
          },
        ),
      )
    },
    loginUser(usernameOrEmail: string, password: string, config?: KOJAxiosRequestConfig) {
      return data(
        this.axios.post(
          apiRoute.public.users.login(),
          {
            usernameOrEmail,
            password,
          },
          {
            ...addonConfig,
            ...config,
            headers: {
              'content-type': 'application/json',
            },
          },
        ),
        (res) => {
          KOJStorage.identity(res.headers[KOJStorage.xIdentity])
          setTime(res.data)
          return res.data
        },
      )
    },
    destroy(config?: KOJAxiosRequestConfig): Promise<undefined> {
      return this.axios.delete(apiRoute.users.destroySelf(), { ...addonConfig, ...config })
    },
    existsUsernameOrEmail(usernameOrEmail: string, config?: KOJAxiosRequestConfig) {
      return this.axios
        .head(apiRoute.public.users.stat(usernameOrEmail), { ignoreError: true, ...addonConfig, ...config })
        .then(() => {
          return Promise.resolve(false)
        })
        .catch((error) => {
          if (error.response.status == 409) {
            return Promise.resolve(true)
          } else {
            return Promise.reject(error)
          }
        })
    },
    stat(username: string, config?: KOJAxiosRequestConfig) {
      return data(this.axios.get(apiRoute.public.users.stat(username), { ...addonConfig, ...config }))
    },
    forgetPassword(username: string, email: string, config?: KOJAxiosRequestConfig): Promise<undefined> {
      return this.axios.post(
        apiRoute.public.users.pwd.forget(),
        {
          username,
          email,
        },
        {
          ...addonConfig,
          ...config,
          headers: {
            'content-type': 'application/json',
          },
        },
      )
    },
    resetPassword(
      username: string,
      email: string,
      verifyCode: string,
      newPassword: string,
      config?: KOJAxiosRequestConfig,
    ): Promise<undefined> {
      return this.axios.post(
        apiRoute.public.users.pwd.reset(),
        {
          username,
          email,
          code: verifyCode,
          newPwd: newPassword,
        },
        {
          ...addonConfig,
          ...config,
          headers: {
            'content-type': 'application/json',
          },
        },
      )
    },
    problems(tags?: string[], listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleProblem>> {
      const cf = { ...addonConfig, ...config }
      cf.params = { ...cf.params, ...listConditionAsParam(listCondition) }
      if (tags && tags.length) {
        cf.params.tags = tags.join(',')
      }
      return page(this.axios.get(apiRoute.public.problems.list(), cf))
    },
    problem(id: string, config?: KOJAxiosRequestConfig): Promise<ProblemDetail> {
      return data(this.axios.get(apiRoute.public.problems.detail(id), { ...addonConfig, ...config })).then((problem) => {
        try {
          problem.contentObj = JSON.parse(problem.content)
        } catch {
          return Promise.reject(new ProblemDetailError('content is not json', problem))
        }
        return problem
      })
    },
    updateProblem(
      id: string,
      data: { name?: string; content?: string; spj?: boolean; tags?: string[] },
      config?: KOJAxiosRequestConfig,
    ): Promise<void> {
      return this.axios.patch(apiRoute.admin.problems.detail(id), data, {
        headers: {
          'content-type': 'application/json',
        },
        ...addonConfig,
        ...config,
      })
    },
    tags(listCondition, config) {
      const cf = { ...addonConfig, ...config }
      cf.params = { ...cf.params, ...listConditionAsParam(listCondition) }
      return page(this.axios.get(apiRoute.public.tags.list(), cf))
    },
    submits(listCondition: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleSubmit>> {
      const cf = { ...addonConfig, ...config }
      cf.params = { ...cf.params, ...listConditionAsParam(listCondition) }
      return page(this.axios.get(apiRoute.public.submits.list(), cf))
    },
    submitDetail(id: string, config?: KOJAxiosRequestConfig): Promise<SubmitDetail> {
      return data(this.axios.get(apiRoute.submits.detail(id), config))
    },
    submit(submitRequest: SubmitRequest, config?: KOJAxiosRequestConfig): Promise<void> {
      return this.axios.put(apiRoute.submits.request(), submitRequest, {
        ...addonConfig,
        ...config,
      })
    },
    languages(config?: KOJAxiosRequestConfig): Promise<string[]> {
      // debugger
      return data(this.axios.get(apiRoute.public.submits.languages.list(), { ...addonConfig, ...config }))
    },
    userList(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<UserManageDetail>> {
      return page(
        this.axios.get(apiRoute.admin.users.list(), {
          ...addonConfig,
          ...config,
          params: {
            ...listConditionAsParam(listCondition),
          },
        }),
      )
    },
    manageUpdateUser(
      userId: string,
      data: { username?: string; email?: string; password?: string; type?: UserType; blocked?: boolean },
      config?: KOJAxiosRequestConfig,
    ): Promise<AxiosResponse> {
      return this.axios.patch(apiRoute.admin.users.update(userId), null, {
        params: data,
        ...addonConfig,
        ...config,
      })
    },
    rank(limit?: number, config?: KOJAxiosRequestConfig): Promise<UserRankInfo[]> {
      if (limit === undefined) {
        return data(this.axios.get(apiRoute.public.users.rank(), { ...addonConfig, ...config }))
      } else {
        return data(
          this.axios.get(apiRoute.public.users.rank(), {
            ...addonConfig,
            ...config,
            params: { limit },
          }),
        )
      }
    },
  }
}

const _axios = http({
  baseURL: '/api',
  timeout: 5000,
})
export default createAPIInstance(_axios)
