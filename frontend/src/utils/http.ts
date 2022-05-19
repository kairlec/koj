import axios, { AxiosError, AxiosInstance, AxiosInterceptorManager, AxiosPromise, AxiosRequestConfig, AxiosResponse } from 'axios'
import { KOJStorage } from './storage'

export interface KOJAxiosRequestConfig<D = any> extends AxiosRequestConfig<D> {
  ignoreError?: boolean
}

export interface KOJAxiosResponse<D = any> extends AxiosResponse<D> {
  config: KOJAxiosRequestConfig<D>
}

export interface KOJAxiosInstance extends AxiosInstance {
  interceptors: {
    request: AxiosInterceptorManager<KOJAxiosRequestConfig>
    response: AxiosInterceptorManager<KOJAxiosResponse>
  }

  (config: KOJAxiosRequestConfig): AxiosPromise

  (url: string, config?: KOJAxiosRequestConfig): AxiosPromise

  getUri(config?: KOJAxiosRequestConfig): string

  request<T = any, R = KOJAxiosResponse<T>, D = any>(config: KOJAxiosRequestConfig<D>): Promise<R>

  get<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  delete<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  head<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  options<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, config?: KOJAxiosRequestConfig<D>): Promise<R>

  post<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, data?: D, config?: KOJAxiosRequestConfig<D>): Promise<R>

  put<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, data?: D, config?: KOJAxiosRequestConfig<D>): Promise<R>

  patch<T = any, R = KOJAxiosResponse<T>, D = any>(url: string, data?: D, config?: KOJAxiosRequestConfig<D>): Promise<R>
}

function showError(error: AxiosError) {
  if (!error.response) {
    if (error.message !== 'canceled') {
      ElMessage({
        type: 'error',
        message: error.message,
      })
    }
    return Promise.reject(error)
  }
  const data = error.response.data as any
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
}

function http(config?: KOJAxiosRequestConfig): KOJAxiosInstance {
  const _http = axios.create(config)

  _http.interceptors.request.use((config) => {
    const id = KOJStorage.identity()
    if (id) {
      config.headers = {
        'x-identity': id,
        ...config.headers,
      }
    }
    config.validateStatus = (status) => {
      return status < 400
    }
    return config
  })

  _http.interceptors.response.use(
    (res) => {
      if (~~(res.status / 100) === 2 || res.status === 304) {
        return Promise.resolve(res)
      } else {
        if (res.status >= 400) {
          return Promise.reject(res)
        } else {
          return Promise.resolve(res)
        }
      }
    },
    (error) => {
      console.debug(error)
      error.showError = () => {
        return showError(error)
      }
      if (error?.config?.ignoreError === true) {
        return error.showError()
      }
      return Promise.reject(error)
    },
  )

  return _http
}

export default http
