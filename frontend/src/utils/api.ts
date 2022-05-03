import http, { IgnoreErrorAbleAxiosRequestConfig } from './http'
import { stringify } from 'qs'
import { KOJStorage } from './storage'
import { AxiosPromise, AxiosRequestConfig, AxiosResponse } from 'axios'

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
  registerUser(username: string, password: string, email: string, config?: IgnoreErrorAbleAxiosRequestConfig): Promise<number>

  loginUser(usernameOrEmail: string, password: string, config?: IgnoreErrorAbleAxiosRequestConfig): Promise<User>

  destroy(config?: IgnoreErrorAbleAxiosRequestConfig): Promise<undefined>

  stat(username: string, config?: IgnoreErrorAbleAxiosRequestConfig): Promise<UserStat>

  existsUsernameOrEmail(usernameOrEmail: string, config?: IgnoreErrorAbleAxiosRequestConfig): Promise<boolean>

  self(config?: IgnoreErrorAbleAxiosRequestConfig): Promise<User>

  forgetPassword(username: string, email: string, config?: IgnoreErrorAbleAxiosRequestConfig): Promise<undefined>

  resetPassword(
    username: string,
    email: string,
    verifyCode: string,
    newPassword: string,
    config?: IgnoreErrorAbleAxiosRequestConfig,
  ): Promise<undefined>
}

// function logger(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
//     const original = descriptor.value;
//
//     descriptor.value = function (...args: any[]) {
//         console.log('params: ', ...args);
//         const result = original.call(this, ...args);
//         console.log('result: ', result);
//         return result;
//     }
// }
//

const axios = http()

let api: IApi

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

;(() => {
  const _public = '/public'
  const _user = '/users'
  const login = `${_public}${_user}`
  const register = `${_public}${_user}`
  const destroy = `${_user}`
  const stat = `${_public}${_user}/`
  const self = `${_user}/self`
  const forgetPassword = `${_public}${_user}/pwd:forget`
  const resetPassword = `${_public}${_user}/pwd:reset`

  api = {
    self(config?: IgnoreErrorAbleAxiosRequestConfig) {
      return data(axios.get(self, { ignoreError: true, ...config }), extraCrateTime) as Promise<User>
    },
    registerUser: (username: string, password: string, email: string, config?: IgnoreErrorAbleAxiosRequestConfig) => {
      return data(
        axios.put(
          register,
          stringify({
            username,
            password,
            email,
          }),
          config
        ),
      )
    },
    loginUser: (usernameOrEmail: string, password: string, config?: IgnoreErrorAbleAxiosRequestConfig) => {
      return data(
        axios.post(
          login,
          stringify({
            usernameOrEmail,
            password,
          }),
          config
        ),
        (res) => {
          console.log(res)
          KOJStorage.identity(res.headers[KOJStorage.xIdentity])
          res.data.createTime = new Date(res.data.createTime)
          return res.data
        },
      )
    },
    destroy: (config?: IgnoreErrorAbleAxiosRequestConfig) => axios.delete(destroy, config),
    existsUsernameOrEmail(usernameOrEmail: string, config?: IgnoreErrorAbleAxiosRequestConfig) {
      return axios
        .head(`${stat}${usernameOrEmail}`, { ignoreError: true, ...config })
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
    stat(username: string, config?: IgnoreErrorAbleAxiosRequestConfig) {
      return data(axios.get(`${stat}${username}`, config), extraCrateTime) as Promise<UserStat>
    },
    forgetPassword(username: string, email: string, config?: IgnoreErrorAbleAxiosRequestConfig): Promise<undefined> {
      return axios.post(forgetPassword, stringify({ username, email }), config)
    },
    resetPassword(
      username: string,
      email: string,
      verifyCode: string,
      newPassword: string,
      config?: IgnoreErrorAbleAxiosRequestConfig
    ): Promise<undefined> {
      return axios.post(resetPassword, stringify({ username, email, code: verifyCode, newPwd: newPassword }), config)
    },
  }
})()

export default api
