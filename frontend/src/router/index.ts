import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../pages/Home.vue'
import Submit from '../pages/Submit.vue'
import Problem from '../pages/Problem.vue'

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
    component: Problem,
  },
  {
    path: '/submit',
    name: 'Submit',
    displayName: '提交',
    component: Submit,
    children: [
      {
        path: '/submit/:id',
        name: 'SubmitDetail',
        component: Submit,
      },
    ]
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
