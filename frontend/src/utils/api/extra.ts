import { AxiosPromise, AxiosResponse } from 'axios'
import { PageData } from '~/apiDeclaration'
import moment from 'moment-timezone'

function defaultExtra<T = any>(res: AxiosResponse<T>): T {
  setTime(res.data)
  return res.data
}

export function data<T = any>(promise: AxiosPromise<T>, extra: (res: AxiosResponse<T>) => T = defaultExtra): Promise<T> {
  return promise.then((res) => {
    if (res) {
      return extra(res)
    } else {
      return res
    }
  })
}

export function defaultExtraPage<T = any>(res: AxiosResponse<T[]>): PageData<T> {
  const totalCount = parseInt(res.headers['x-total-count'])
  setTime(res.data)
  return {
    totalCount,
    record: res.data,
  }
}

export function page<T = any>(
  promise: AxiosPromise<T[]>,
  extra: (res: AxiosResponse<T[]>) => PageData<T> = defaultExtraPage,
): Promise<PageData<T>> {
  return promise.then((res) => {
    return extra(res)
  })
}

const localTimezone = moment.tz.guess()

export function setTime(obj: Record<string, any> | Record<string, any>[]) {
  if (Array.isArray(obj)) {
    obj.forEach((it) => setTime(it))
    return
  }
  for (const key in obj) {
    if (typeof obj[key] === 'object') {
      setTime(obj[key])
    }
    if (key === 'createTime' || key === 'updateTime' || key === 'startTime' || key === 'endTime') {
      obj[key] = moment.tz(obj[key], 'Atlantic/Reykjavik').tz(localTimezone)
    }
  }
}
