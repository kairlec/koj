export interface ITime {
  createTime: Date
  updateTime?: Date
}

export interface UserStat extends ITime {
  id: number
  username: string
  submitted: number
  ac: number[]
}

export interface User extends ITime {
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

export interface ProblemSampleAns {
  input: string
  output: string
}

export interface ProblemContent {
  description: string
  input: string
  output: string
  samples: ProblemSampleAns[]
  hint?: string
}

export interface ProblemConfig {
  languageId: string
  memoryLimit: number
  timeLimit: number
  createTime: Date
  updateTime: Date
}

export interface ProblemDetail {
  id: number
  name: string
  content: string
  contentObj: ProblemContent
  spj: boolean
  createTime: string
  updateTime: string
  config: ProblemConfig[]
  idx?: number
  tags: string[]
}

export interface Tag {
  id: number
  name: string
}

export interface ProblemConfigManage {
  languageId: string
  time: number
  memory: number
  maxOutputSize?: number
  maxStack?: number
  maxProcessNumber?: number
  args?: string[]
  env?: string[]
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
