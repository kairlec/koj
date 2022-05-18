import { Moment } from 'moment'

export interface ITime {
  createTime: Moment
  updateTime?: Moment
}

export interface UserStat extends ITime {
  id: string
  username: string
  submitted: number
  ac: number[]
}

export interface User extends ITime {
  id: string
  username: string
  email: string
  type: number
}

export interface PageData<T> {
  totalCount: number
  record: T[]
}

export interface SimpleProblem {
  id: string
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
  createTime: Moment
  updateTime: Moment
}

export interface ProblemDetail {
  id: string
  name: string
  content: string
  contentObj: ProblemContent
  spj: boolean
  createTime: Moment
  updateTime: Moment
  config: ProblemConfig[]
  idx?: number
  tags: string[]
}

export interface Tag {
  id: string
  name: string
}

export interface ProblemConfigManage {
  languageId: string
  time: number
  memory: number
  maxOutputSize?: string
  maxStack?: string
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

export type SubmitState =
  | 'IN_QUEUE'
  | 'IN_COMPILING'
  | 'IN_RUNNING'
  | 'IN_JUDGING'
  | 'WRONG_ANSWER'
  | 'ACCEPTED'
  | 'TIME_LIMIT_EXCEEDED'
  | 'MEMORY_LIMIT_EXCEEDED'
  | 'OUTPUT_LIMIT_EXCEEDED'
  | 'RUNTIME_ERROR'
  | 'SYSTEM_ERROR'
  | 'COMPILATION_ERROR'
  | 'PRESENTATION_ERROR'
  | 'UNKNOWN'
  | 'NO_SANDBOX'

export function submitStateToString(state: SubmitState): string {
  switch (state) {
    case 'IN_QUEUE':
      return '队列中'
    case 'IN_COMPILING':
      return '编译中'
    case 'IN_RUNNING':
      return '运行中'
    case 'IN_JUDGING':
      return '评测中'
    case 'WRONG_ANSWER':
      return 'WA'
    case 'ACCEPTED':
      return 'AC'
    case 'TIME_LIMIT_EXCEEDED':
      return 'TLE'
    case 'MEMORY_LIMIT_EXCEEDED':
      return 'MLE'
    case 'OUTPUT_LIMIT_EXCEEDED':
      return 'OLE'
    case 'RUNTIME_ERROR':
      return 'RE'
    case 'SYSTEM_ERROR':
      return 'SE'
    case 'COMPILATION_ERROR':
      return 'CE'
    case 'PRESENTATION_ERROR':
      return 'PE'
    case 'UNKNOWN':
      return '未知错误'
    case 'NO_SANDBOX':
      return '沙盒不可用'
  }
}

export interface SimpleSubmit {
  id: string
  problemId: string
  state: SubmitState
  castMemory?: string
  castTime?: string
  languageId: string
  belongUserId: string
  username: string
  createTime: Moment
  updateTime: Moment
}

export interface SubmitRequest {
  competitionId?: string
  languageId: string
  problemId: string
  code: string
}

export interface SubmitDetail {
  id: string
  problemId: string
  state: string
  castMemory?: string
  castTime?: string
  languageId: string
  belongUserId: string
  username: string
  createTime: Moment
  updateTime: Moment
  code: string
  stderr?: string
}

export enum UserType {
  ADMIN = 0,
  USER = 1,
}

export interface UserManageDetail {
  id: string
  username: string
  email: string
  type: UserType
  createTime: Moment
  updateTime: Moment
  blocked: number
}

export interface UserRankInfo {
  userId: string
  username: string
  rank: number
  ac: number
}
