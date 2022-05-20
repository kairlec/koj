import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import KojHome from '../pages/KojHome.vue'
import SubmitList from '../pages/SubmitList.vue'
import ProblemList from '../pages/ProblemList.vue'
import ProblemDetail from '../pages/ProblemDetail.vue'
import UserList from '../pages/UserList.vue'
import CompetitionList from '../pages/CompetitionList.vue'
import CompetitionProblemList from '../pages/CompetitionProblemList.vue'

export const routes: Array<RouteRecordRaw & { displayName?: String; manage?: boolean }> = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    name: 'KojHome',
    displayName: '首页',
    component: KojHome,
  },
  {
    path: '/problem',
    name: 'ProblemList',
    displayName: '题目',
    component: ProblemList,
  },
  {
    path: '/problem/:id',
    name: 'ProblemDetail',
    component: ProblemDetail,
  },
  {
    path: '/competition/:id/problem',
    name: 'CompetitionProblemList',
    component: CompetitionProblemList,
  },
  {
    path: '/submit',
    name: 'SubmitList',
    displayName: '提交',
    component: SubmitList,
  },
  {
    path: '/competition',
    name: 'CompetitionList',
    displayName: '比赛',
    component: CompetitionList,
  },
  {
    path: '/userManage',
    name: 'UserManage',
    displayName: '用户管理',
    component: UserList,
    manage: true,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
