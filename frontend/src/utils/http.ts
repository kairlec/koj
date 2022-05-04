import axios, { AxiosInstance, AxiosInterceptorManager, AxiosPromise, AxiosRequestConfig, AxiosResponse } from 'axios'
import { KOJStorage } from './storage'

export interface KOJAxiosRequestConfig<D = any> extends AxiosRequestConfig<D> {
  ignoreError?: boolean
}

export interface KOJAxiosInstance extends AxiosInstance {
  interceptors: {
    request: AxiosInterceptorManager<KOJAxiosRequestConfig>
    response: AxiosInterceptorManager<AxiosResponse>
  }

  (config: KOJAxiosRequestConfig): AxiosPromise

  (url: string, config?: KOJAxiosRequestConfig): AxiosPromise

  getUri(config?: KOJAxiosRequestConfig): string

  request<T = any, R = AxiosResponse<T>, D = any>(config: KOJAxiosRequestConfig<D>): Promise<R>

  get<T = any, R = AxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  delete<T = any, R = AxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  head<T = any, R = AxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  options<T = any, R = AxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  post<T = any, R = AxiosResponse<T>, D = any>(url: string, data?: D, config?: KOJAxiosRequestConfig<D>): Promise<R>

  put<T = any, R = AxiosResponse<T>, D = any>(url: string, data?: D, config?: KOJAxiosRequestConfig<D>): Promise<R>

  patch<T = any, R = AxiosResponse<T>, D = any>(url: string, data?: D, config?: KOJAxiosRequestConfig<D>): Promise<R>
}

function http(config?: KOJAxiosRequestConfig): KOJAxiosInstance {
  const _http = axios.create(config)

  _http.interceptors.request.use(
    (config) => {
      console.log('config', config)
      const id = KOJStorage.identity()
      if (id) {
        config.headers = {
          'x-identity': id,
          ...config.headers,
        }
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    },
  )

  _http.interceptors.response.use(
    (res) => {
      if (res.status / 100 == 2 || res.status == 304) {
        return Promise.resolve(res)
      }
    },
    (error) => {
      console.debug(error)
      if (error.config?.ignoreError) {
        return Promise.reject(error)
      }
      if (!error.response) {
        if (error.message !== 'canceled') {
          ElMessage({
            type: 'error',
            message: error.message,
          })
        }
        return Promise.reject(error)
      }
      const data = error.response.data
      if (data?.code) {
        ElMessage({
          type: 'error',
          message: `[${data.code}] ${data.message}`,
        })
        return Promise.reject(error)
      }
      switch (error.response.status) {
        case 404:
          ElMessage({
            type: 'error',
            message: '资源不存在',
          })
          break
        case 500:
          ElMessage({
            type: 'error',
            message: '服务器故障',
          })
          break
        case 502:
          ElMessage({
            type: 'error',
            message: '服务器正在维护',
          })
          break
        default:
          ElMessage({
            type: 'error',
            message: '请求出错',
          })
          break
      }
      return Promise.reject(error)
    },
  )

  return _http
}

export default http
