import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../pages/Home.vue'
import Submit from '../pages/Submit.vue'
import ProblemList from '../pages/ProblemList.vue'
import ProblemDetail from '../pages/ProblemDetail.vue'

export const routes: Array<RouteRecordRaw & { displayName?: String; manage?: boolean }> = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    name: 'Home',
    displayName: '首页',
    component: Home,
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
    component: Submit,
    manage: true,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
