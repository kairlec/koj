import http, { KOJAxiosInstance, KOJAxiosRequestConfig } from './http'
import { stringify } from 'qs'
import { KOJStorage } from './storage'
import { AxiosPromise, AxiosResponse } from 'axios'

export interface ICreateTime {
  createTime: Date
}

export interface UserStat extends ICreateTime {
  id: number
  username: string
  submitted: number
  ac: number[]
}

export interface User extends ICreateTime {
  id: number
  username: string
  email: string
  type: number
}

export interface PageData<T> {
  totalCount: number
  record: T[]
}

export interface SimpleProblem {
  id: number
  name: string
  spj: boolean
  idx?: number
  tags: string[]
}

export type SearchCondition = Record<
  string,
  {
    mode: 'Fuzzy' | 'Precise' | 'StartsWith' | 'EndsWith' | 'NoEscape'
    value: string
  }
>

export type SortCondition = Record<string, 'Asc' | 'Desc'>

export interface ListCondition {
  search?: SearchCondition
  sort?: SortCondition
  limit?: number
  seek?: any[]
}

function wrapSearch(search?: SearchCondition): string[] | undefined {
  if (search) {
    const target: string[] = []
    for (const key in search) {
      const cur = search[key]
      switch (cur.mode) {
        case 'Fuzzy':
          target.push(`0${key}=${cur.value}`)
          break
        case 'Precise':
          target.push(`1${key}=${cur.value}`)
          break
        case 'StartsWith':
          target.push(`2${key}=${cur.value}`)
          break
        case 'EndsWith':
          target.push(`3${key}=${cur.value}`)
          break
        case 'NoEscape':
          target.push(`4${key}=${cur.value}`)
          break
      }
    }
    return target
  }
}

function wrapSort(sort?: SortCondition): string[] | undefined {
  if (sort) {
    const target: string[] = []
    for (const key in sort) {
      const cur = sort[key]
      switch (cur) {
        case 'Asc':
          target.push(`-${key}`)
          break
        case 'Desc':
          target.push(`~${key}`)
          break
      }
    }
    return target
  }
}

export function listConditionAsParam(
  condition?: ListCondition,
): Record<'search' | 'sort' | 'limit' | 'seek', string | undefined> | undefined {
  if (condition) {
    return {
      search: wrapSearch(condition.search)?.join(','),
      sort: wrapSort(condition.sort)?.join(','),
      limit: condition.limit?.toString(),
      seek: condition.seek?.join(','),
    }
  }
}

interface IApi {
  withConfig(config: KOJAxiosRequestConfig): IApi

  axios: KOJAxiosInstance

  registerUser(username: string, password: string, email: string, config?: KOJAxiosRequestConfig): Promise<number>

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
}

const _axios = http({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  timeout: 5000,
})

function defaultExtra<T = any>(res: AxiosResponse<T>): T {
  return res.data
}

function data<T = any>(promise: AxiosPromise<T>, extra: (res: AxiosResponse<T>) => T = defaultExtra): Promise<T> {
  return promise.then((res) => {
    return extra(res)
  })
}

function defaultExtraPage<T = any>(res: AxiosResponse<T[]>): PageData<T> {
  const totalCount = parseInt(res.headers['x-total-count'])
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

function extraCrateTime(res: AxiosResponse): ICreateTime {
  const user = res.data
  user.createTime = new Date(user.createTime)
  return user
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
  users: {
    _base: '',
    destroySelf() {
      return `${this._base}`
    },
    self() {
      return `${this._base}/self`
    },
  },
  public: {
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
    },
  },
})

function createAPIInstance(axiosInstance: KOJAxiosInstance, addonConfig?: KOJAxiosRequestConfig): IApi {
  return {
    withConfig(config: KOJAxiosRequestConfig): IApi {
      return createAPIInstance(axiosInstance, config)
    },
    axios: axiosInstance,
    self(config?: KOJAxiosRequestConfig) {
      return data(this.axios.get(apiRoute.users.self(), { ignoreError: true, ...config }), extraCrateTime) as Promise<User>
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
          console.log(res)
          KOJStorage.identity(res.headers[KOJStorage.xIdentity])
          res.data.createTime = new Date(res.data.createTime)
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
      return data(this.axios.get(apiRoute.public.users.stat(username), { ...addonConfig, ...config }), extraCrateTime) as Promise<UserStat>
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
        debugger
        cf.params.tags = tags.join(',')
      }
      return page(this.axios.get(apiRoute.public.problems.list(), cf))
    },
  }
}

export default createAPIInstance(_axios)
