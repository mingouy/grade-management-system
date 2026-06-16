import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'admin/users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', roles: ['admin'] }
      },
      {
        path: 'admin/departments',
        name: 'DepartmentManage',
        component: () => import('@/views/admin/DepartmentManage.vue'),
        meta: { title: '专业管理', roles: ['admin'] }
      },
      {
        path: 'admin/classes',
        name: 'ClassManage',
        component: () => import('@/views/admin/ClassManage.vue'),
        meta: { title: '班级管理', roles: ['admin'] }
      },
      {
        path: 'admin/courses',
        name: 'CourseManage',
        component: () => import('@/views/admin/CourseManage.vue'),
        meta: { title: '课程管理', roles: ['admin'] }
      },
      {
        path: 'admin/teachers',
        name: 'TeacherManage',
        component: () => import('@/views/admin/TeacherManage.vue'),
        meta: { title: '教师管理', roles: ['admin'] }
      },
      {
        path: 'admin/students',
        name: 'StudentManage',
        component: () => import('@/views/admin/StudentManage.vue'),
        meta: { title: '学生管理', roles: ['admin'] }
      },
      {
        path: 'teacher/classes',
        name: 'MyClass',
        component: () => import('@/views/teacher/MyClass.vue'),
        meta: { title: '我的班级', roles: ['teacher'] }
      },
      {
        path: 'teacher/score-input',
        name: 'ScoreInput',
        component: () => import('@/views/teacher/ScoreInput.vue'),
        meta: { title: '成绩录入', roles: ['teacher'] }
      },
      {
        path: 'teacher/score-query',
        name: 'ScoreQuery',
        component: () => import('@/views/teacher/ScoreQuery.vue'),
        meta: { title: '成绩查询', roles: ['teacher'] }
      },
      {
        path: 'student/info',
        name: 'MyInfo',
        component: () => import('@/views/student/MyInfo.vue'),
        meta: { title: '个人信息', roles: ['student'] }
      },
      {
        path: 'student/courses',
        name: 'MyCourse',
        component: () => import('@/views/student/MyCourse.vue'),
        meta: { title: '我的课程', roles: ['student'] }
      },
      {
        path: 'student/scores',
        name: 'MyScore',
        component: () => import('@/views/student/MyScore.vue'),
        meta: { title: '我的成绩', roles: ['student'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.public) {
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  if (to.meta.roles && !to.meta.roles.includes(userStore.role)) {
    next('/dashboard')
    return
  }

  next()
})

export default router
