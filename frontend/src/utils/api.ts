import http, { KOJAxiosInstance, KOJAxiosRequestConfig } from './http'
import { stringify } from 'qs'
import { KOJStorage } from './storage'
import { AxiosPromise, AxiosResponse } from 'axios'
import {
  ITime,
  ListCondition,
  listConditionAsParam,
  PageData,
  ProblemConfigManage,
  ProblemDetail,
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
import moment from 'moment'

interface IApi {
  withConfig(config: KOJAxiosRequestConfig): IApi

  axios: KOJAxiosInstance

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

  problems(tags?: string[], listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleProblem>>

  problem(id: string, config?: KOJAxiosRequestConfig): Promise<ProblemDetail>

  updateProblem(
    id: string,
    data: { name?: string; content?: string; spj?: boolean; tags?: string[] },
    config?: KOJAxiosRequestConfig,
  ): Promise<void>

  tags(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<Tag>>

  submits(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<SimpleSubmit>>

  submit(submitRequest: SubmitRequest, config?: KOJAxiosRequestConfig): Promise<void>

  submitDetail(id: string, config?: KOJAxiosRequestConfig): Promise<SubmitDetail>

  languages(config?: KOJAxiosRequestConfig): Promise<string[]>

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

  userList(listCondition?: ListCondition, config?: KOJAxiosRequestConfig): Promise<PageData<UserManageDetail>>

  manageUpdateUser(
    userId: string,
    data: { username?: string; email?: string; password?: string; type?: UserType; blocked?: boolean },
    config?: KOJAxiosRequestConfig,
  ): Promise<AxiosResponse>

  rank(limit?: number, config?: KOJAxiosRequestConfig): Promise<UserRankInfo[]>
}

const _axios = http({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  timeout: 5000,
})

function defaultExtra<T = any>(res: AxiosResponse<T>): T {
  setTime(res.data)
  return res.data
}

function data<T = any>(promise: AxiosPromise<T>, extra: (res: AxiosResponse<T>) => T = defaultExtra): Promise<T> {
  return promise.then((res) => {
    if (res) {
      return extra(res)
    } else {
      return res
    }
  })
}

function defaultExtraPage<T = any>(res: AxiosResponse<T[]>): PageData<T> {
  const totalCount = parseInt(res.headers['x-total-count'])
  setTime(res.data)
  return {
    totalCount,
    record: res.data,
  }
}

function page<T = any>(
  promise: AxiosPromise<T[]>,
  extra: (res: AxiosResponse<T[]>) => PageData<T> = defaultExtraPage,
): Promise<PageData<T>> {
  return promise.then((res) => {
    return extra(res)
  })
}

function setTime(obj: Record<string, any> | Record<string, any>[]) {
  if (Array.isArray(obj)) {
    obj.forEach((it) => setTime(it))
    return
  }
  for (const key in obj) {
    if (typeof obj[key] === 'object') {
      setTime(obj[key])
    }
    if (key === 'createTime' || key === 'updateTime') {
      obj[key] = moment(obj[key])
    }
  }
}

function extraTime<T>(res: AxiosResponse): T & ITime {
  const data = res.data
  setTime(data)
  return data
}

function wrapRecord<T extends Record<string, any>>(base: T, parent = ''): T {
  for (const key in base) {
    const value = base[key] as any
    if (typeof value === 'string') {
      if (value.length === 0) {
        ;(base[key] as any) = `${parent}`
      } else if (value.startsWith('/')) {
        ;(base[key] as any) = `${parent}${key}`
      } else {
        ;(base[key] as any) = `${parent}/${key}`
      }
    } else if (typeof value === 'object') {
      wrapRecord(value, `${parent}/${key}`)
    }
  }
  return base
}

const apiRoute = wrapRecord({
  admin: {
    users: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
      update(userId: string) {
        return `${this._base}/${userId}`
      },
    },
    problems: {
      _base: '',
      base() {
        return this._base
      },
      detail(id: string) {
        return `${this._base}/${id}`
      },
      withTag(problemId: string, tagId: string) {
        return `${this._base}/${problemId}/tags/${tagId}`
      },
      configList(problemId: string) {
        return `${this._base}/${problemId}/configs/-`
      },
      configs(problemId: string) {
        return `${this._base}/${problemId}/configs`
      },
      config(problemId: string, languageId: string) {
        return `${this._base}/${problemId}/configs/${languageId}`
      },
      runs(problemId: string) {
        return `${this._base}/${problemId}/runs`
      },
    },
    tags: {
      _base: '',
      base() {
        return this._base
      },
      detail(tagId: string) {
        return `${this._base}/${tagId}`
      },
    },
  },
  users: {
    _base: '',
    destroySelf() {
      return `${this._base}`
    },
    self() {
      return `${this._base}/self`
    },
  },
  submits: {
    _base: '',
    request() {
      return `${this._base}`
    },
    detail(id: string) {
      return `${this._base}/${id}`
    },
  },
  public: {
    submits: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
      languages: {
        _base: '',
        list() {
          return `${this._base}/-`
        },
      },
    },
    users: {
      _base: '',
      login() {
        return this._base
      },
      register() {
        return this._base
      },
      stat(usernameOrEmail: string) {
        return `${this._base}/${usernameOrEmail}`
      },
      rank() {
        return `${this._base}/rank`
      },
      pwd: {
        _base: '',
        forget() {
          return `${this._base}:forget`
        },
        reset() {
          return `${this._base}:reset`
        },
      },
    },
    problems: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
      detail(id: string) {
        return `${this._base}/${id}`
      },
    },
    tags: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
    },
  },
})

export class ProblemDetailError extends Error {
  constructor(message: string, public problem: ProblemDetail) {
    super(message)
    this.name = 'ProblemDetailError'
  }
}

function createAPIInstance(axiosInstance: KOJAxiosInstance, addonConfig?: KOJAxiosRequestConfig): IApi {
  return {
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
      return createAPIInstance(axiosInstance, config)
    },
    axios: axiosInstance,
    self(config?: KOJAxiosRequestConfig) {
      return data(this.axios.get(apiRoute.users.self(), { ignoreError: true, ...config }), extraTime)
    },
    registerUser(username: string, password: string, email: string, config?: KOJAxiosRequestConfig) {
      return data(
        this.axios.put(
          apiRoute.public.users.register(),
          stringify({
            username,
            password,
            email,
          }),
          { ...addonConfig, ...config },
        ),
      )
    },
    loginUser(usernameOrEmail: string, password: string, config?: KOJAxiosRequestConfig) {
      return data(
        this.axios.post(
          apiRoute.public.users.login(),
          stringify({
            usernameOrEmail,
            password,
          }),
          { ...addonConfig, ...config },
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
      return data(this.axios.get(apiRoute.public.users.stat(username), { ...addonConfig, ...config }), extraTime)
    },
    forgetPassword(username: string, email: string, config?: KOJAxiosRequestConfig): Promise<undefined> {
      return this.axios.post(
        apiRoute.public.users.pwd.forget(),
        stringify({
          username,
          email,
        }),
        { ...addonConfig, ...config },
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
        stringify({
          username,
          email,
          code: verifyCode,
          newPwd: newPassword,
        }),
        {
          ...addonConfig,
          ...config,
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
      return data(this.axios.get(apiRoute.public.problems.detail(id), { ...addonConfig, ...config }), extraTime).then((problem) => {
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
        headers: {
          'content-type': 'application/json',
        },
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
        return data(this.axios.get(apiRoute.public.users.rank(), { ...addonConfig, ...config, params: { limit } }))
      }
    },
  }
}

export default createAPIInstance(_axios)
