import { AppConfig, reactive } from 'vue'
import { UnwrapNestedRefs } from '@vue/reactivity'
import { Moment } from 'moment'

interface GlobalUser {
  createTime: Moment
  email: string
  id: string
  type: number
  username: string
}

interface UserReactive {
  user: GlobalUser | null
  initing: boolean
}

interface AppConfigProvider {
  config: AppConfig
}

export function getGlobalUser(instance: AppConfigProvider): UnwrapNestedRefs<UserReactive> {
  const properties = instance.config.globalProperties
  const globalUser = properties['$user']
  if (!globalUser) {
    const newGlobal = reactive<UserReactive>({
      user: null,
      initing: false,
    })
    properties['$user'] = newGlobal
    return newGlobal
  }
  return globalUser
}

export function setGlobalUser(instance: AppConfigProvider, user: GlobalUser | null, initing?: boolean) {
  const gbr = getGlobalUser(instance)
  gbr.user = user
  if (initing !== undefined) {
    gbr.initing = initing
  }
}
