import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import KojHome from '../pages/KojHome.vue'
import Submit from '../pages/SubmitList.vue'
import ProblemList from '../pages/ProblemList.vue'
import ProblemDetail from '../pages/ProblemDetail.vue'
import UserList from '../pages/UserList.vue'

export const routes: Array<RouteRecordRaw & { displayName?: String; manage?: boolean }> = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    name: 'Home',
    displayName: '首页',
    component: KojHome,
  },
  {
    path: '/problem',
    name: 'Problem',
    displayName: '题目',
    component: ProblemList,
  },
  {
    path: '/problem/:id',
    name: 'ProblemDetail',
    component: ProblemDetail,
  },
  {
    path: '/submit',
    name: 'Submit',
    displayName: '提交',
    component: Submit,
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
