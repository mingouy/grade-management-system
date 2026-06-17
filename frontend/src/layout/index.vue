<template>
  <div class="layout-container">
    <aside :class="['sidebar', { collapsed: isCollapsed }]">
      <div class="sidebar-header" @click="$router.push('/dashboard')">
        <svg viewBox="0 0 1024 1024" width="28" height="28">
          <path d="M576 384h256v256h-256z" fill="#fff"/>
          <path d="M384 192h256v512H384z" fill="rgba(255,255,255,0.3)"/>
          <path d="M448 256h128v384H448z" fill="#fff"/>
        </svg>
        <transition name="fade">
          <span v-if="!isCollapsed" class="brand-name">MyGrade</span>
        </transition>
      </div>

      <nav class="sidebar-nav">
        <div v-if="userStore.role === 'admin'" class="nav-group">
          <div class="nav-section-title" v-if="!isCollapsed">系统管理</div>
          <div
            v-for="item in adminMenus"
            :key="item.path"
            :class="['nav-item', { active: activeMenu === item.path }]"
            @click="router.push(item.path)"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <transition name="fade">
              <span v-if="!isCollapsed" class="nav-label">{{ item.label }}</span>
            </transition>
          </div>
        </div>

        <div v-if="userStore.role === 'teacher'" class="nav-group">
          <div class="nav-section-title" v-if="!isCollapsed">教学管理</div>
          <div
            v-for="item in teacherMenus"
            :key="item.path"
            :class="['nav-item', { active: activeMenu === item.path }]"
            @click="router.push(item.path)"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <transition name="fade">
              <span v-if="!isCollapsed" class="nav-label">{{ item.label }}</span>
            </transition>
          </div>
        </div>

        <div v-if="userStore.role === 'student'" class="nav-group">
          <div class="nav-section-title" v-if="!isCollapsed">学生中心</div>
          <div
            v-for="item in studentMenus"
            :key="item.path"
            :class="['nav-item', { active: activeMenu === item.path }]"
            @click="router.push(item.path)"
          >
            <el-icon :size="18"><component :is="item.icon" /></el-icon>
            <transition name="fade">
              <span v-if="!isCollapsed" class="nav-label">{{ item.label }}</span>
            </transition>
          </div>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
          <el-icon :size="16">
            <component :is="isCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>
      </div>
    </aside>

    <div :class="['main-wrapper', { expanded: isCollapsed }]">
      <header class="topbar">
        <div class="topbar-left">
          <div class="mobile-toggle" @click="isCollapsed = !isCollapsed">
            <el-icon :size="20"><Fold /></el-icon>
          </div>
          <div class="breadcrumb-area">
            <span class="page-title">{{ pageTitle }}</span>
          </div>
        </div>
        <div class="topbar-right">
          <NotificationBell />
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-pill">
              <div class="avatar">{{ (userStore.userInfo.realName || userStore.userInfo.username || 'U')[0] }}</div>
              <div class="user-text">
                <span class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username || '用户' }}</span>
                <span class="user-role">{{ roleLabel }}</span>
              </div>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>

    <div v-if="!isCollapsed && isMobile" class="overlay" @click="isCollapsed = true"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import NotificationBell from '@/components/NotificationBell.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapsed = ref(false)
const isMobile = ref(false)

const activeMenu = computed(() => route.path)

const adminMenus = [
  { path: '/admin/users', label: '用户管理', icon: 'User' },
  { path: '/admin/teachers', label: '教师管理', icon: 'Reading' },
  { path: '/admin/students', label: '学生管理', icon: 'UserFilled' },
  { path: '/admin/departments', label: '专业管理', icon: 'OfficeBuilding' },
  { path: '/admin/classes', label: '班级管理', icon: 'Collection' },
  { path: '/admin/courses', label: '课程管理', icon: 'Reading' },
  { path: '/analysis', label: '成绩分析', icon: 'DataAnalysis' }
]

const teacherMenus = [
  { path: '/teacher/classes', label: '我的班级', icon: 'UserFilled' },
  { path: '/teacher/publish-course', label: '发布课程', icon: 'Plus' },
  { path: '/teacher/score-input', label: '成绩录入', icon: 'Edit' },
  { path: '/teacher/score-query', label: '成绩查询', icon: 'DataAnalysis' },
  { path: '/analysis', label: '成绩分析', icon: 'DataAnalysis' }
]

const studentMenus = [
  { path: '/student/info', label: '个人信息', icon: 'User' },
  { path: '/student/select-course', label: '在线选课', icon: 'Grid' },
  { path: '/student/courses', label: '我的课程', icon: 'Reading' },
  { path: '/student/scores', label: '我的成绩', icon: 'Trophy' },
  { path: '/student/score-trend', label: '成绩趋势', icon: 'TrendCharts' }
]

const pageTitle = computed(() => {
  const allMenus = [...adminMenus, ...teacherMenus, ...studentMenus]
  const found = allMenus.find(m => route.path.includes(m.path))
  if (found) return found.label
  if (route.path === '/dashboard') return '首页'
  return '成绩管理系统'
})

const roleLabel = computed(() => {
  const roleMap = { admin: '管理员', teacher: '教师', student: '学生' }
  return roleMap[userStore.role] || ''
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  } else if (command === 'profile') {
    if (userStore.role === 'student') {
      router.push('/student/info')
    } else {
      router.push('/dashboard')
    }
  }
}

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) isCollapsed.value = true
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: var(--mi-bg-card);
  border-right: 1px solid var(--mi-border-color);
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  flex-direction: column;
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  cursor: pointer;
  border-bottom: 1px solid var(--mi-border-color);
  flex-shrink: 0;
  overflow: hidden;
}

.brand-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--mi-primary);
  white-space: nowrap;
  letter-spacing: -0.3px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 8px;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-group {
  margin-bottom: 8px;
}

.nav-section-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--mi-text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
  padding: 8px 12px 6px;
  white-space: nowrap;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--mi-radius-sm);
  cursor: pointer;
  transition: var(--mi-transition);
  color: var(--mi-text-regular);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
  position: relative;
  white-space: nowrap;
}

.nav-item:hover {
  background: var(--mi-bg-page);
  color: var(--mi-text-primary);
}

.nav-item.active {
  background: var(--mi-primary-lighter);
  color: var(--mi-primary);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  background: var(--mi-primary);
  border-radius: 0 2px 2px 0;
}

.sidebar-footer {
  padding: 8px;
  border-top: 1px solid var(--mi-border-color);
  flex-shrink: 0;
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  border-radius: var(--mi-radius-sm);
  cursor: pointer;
  color: var(--mi-text-secondary);
  transition: var(--mi-transition);
}

.collapse-btn:hover {
  background: var(--mi-bg-page);
  color: var(--mi-text-primary);
}

.main-wrapper {
  flex: 1;
  margin-left: 220px;
  transition: margin-left 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-wrapper.expanded {
  margin-left: 60px;
}

.topbar {
  height: 60px;
  background: var(--mi-bg-card);
  border-bottom: 1px solid var(--mi-border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 90;
  flex-shrink: 0;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.mobile-toggle {
  display: none;
  cursor: pointer;
  padding: 6px;
  border-radius: var(--mi-radius-sm);
  color: var(--mi-text-regular);
  transition: var(--mi-transition);
}

.mobile-toggle:hover {
  background: var(--mi-bg-page);
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--mi-text-primary);
}

.topbar-right {
  display: flex;
  align-items: center;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px 5px 5px;
  border-radius: 20px;
  transition: var(--mi-transition);
}

.user-pill:hover {
  background: var(--mi-bg-page);
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--mi-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-text {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--mi-text-primary);
  line-height: 1.2;
}

.user-role {
  font-size: 11px;
  color: var(--mi-text-secondary);
  line-height: 1.2;
}

.arrow {
  color: var(--mi-text-secondary);
  font-size: 12px;
}

.main-content {
  flex: 1;
  padding: 20px 24px;
  background: var(--mi-bg-page);
  overflow-y: auto;
}

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 99;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.15s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    z-index: 200;
  }

  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }

  .main-wrapper {
    margin-left: 0 !important;
  }

  .mobile-toggle {
    display: flex;
  }

  .user-text {
    display: none;
  }

  .main-content {
    padding: 16px;
  }

  .topbar {
    padding: 0 16px;
  }
}
</style>
