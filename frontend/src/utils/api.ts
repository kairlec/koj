import http, { KOJAxiosInstance, KOJAxiosRequestConfig } from './http'
import { stringify } from 'qs'
import { KOJStorage } from './storage'
import { AxiosPromise, AxiosResponse } from 'axios'

interface ICreateTime {
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
}

const _axios = http({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  timeout: 5000
})

function defaultExtra<T = any>(res: AxiosResponse<T>): T {
  return res.data
}

function data<T = any>(promise: AxiosPromise<T>, extra: (res: AxiosResponse<T>) => T = defaultExtra): Promise<T> {
  return promise.then((res) => {
    return extra(res)
  })
}

function extraCrateTime(res: AxiosResponse): ICreateTime {
  const user = res.data
  user.createTime = new Date(user.createTime)
  return user
}

const _public = '/public'
const _user = '/users'
const login = `${_public}${_user}`
const register = `${_public}${_user}`
const destroy = `${_user}`
const stat = `${_public}${_user}/`
const self = `${_user}/self`
const forgetPassword = `${_public}${_user}/pwd:forget`
const resetPassword = `${_public}${_user}/pwd:reset`

function createAPIInstance(axiosInstance: KOJAxiosInstance, addonConfig?: KOJAxiosRequestConfig): IApi {
  return {
    withConfig(config: KOJAxiosRequestConfig): IApi {
      return createAPIInstance(axiosInstance, config)
    },
    axios: axiosInstance,
    self(config?: KOJAxiosRequestConfig) {
      return data(this.axios.get(self, { ignoreError: true, ...config }), extraCrateTime) as Promise<User>
    },
    registerUser(username: string, password: string, email: string, config?: KOJAxiosRequestConfig) {
      return data(
        this.axios.put(
          register,
          stringify({
            username,
            password,
            email
          }),
          { ...addonConfig, ...config }
        ),
      )
    },
    loginUser(usernameOrEmail: string, password: string, config?: KOJAxiosRequestConfig) {
      return data(
        this.axios.post(
          login,
          stringify({
            usernameOrEmail,
            password
          }),
          { ...addonConfig, ...config }
        ),
        (res) => {
          console.log(res)
          KOJStorage.identity(res.headers[KOJStorage.xIdentity])
          res.data.createTime = new Date(res.data.createTime)
          return res.data
        }
      )
    },
    destroy(config?: KOJAxiosRequestConfig): Promise<undefined> {
      return this.axios.delete(destroy, { ...addonConfig, ...config })
    },
    existsUsernameOrEmail(usernameOrEmail: string, config?: KOJAxiosRequestConfig) {
      return this.axios
        .head(`${stat}${usernameOrEmail}`, { ignoreError: true, ...addonConfig, ...config })
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
      return data(this.axios.get(`${stat}${username}`, { ...addonConfig, ...config }), extraCrateTime) as Promise<UserStat>
    },
    forgetPassword(username: string, email: string, config?: KOJAxiosRequestConfig): Promise<undefined> {
      return this.axios.post(forgetPassword, stringify({ username, email }), { ...addonConfig, ...config })
    },
    resetPassword(
      username: string,
      email: string,
      verifyCode: string,
      newPassword: string,
      config?: KOJAxiosRequestConfig
    ): Promise<undefined> {
      return this.axios.post(resetPassword, stringify({ username, email, code: verifyCode, newPwd: newPassword }), {
        ...addonConfig,
        ...config
      })
    }
  } as IApi
}

export default createAPIInstance(_axios)
