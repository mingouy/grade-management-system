<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">你好，{{ userStore.userInfo.realName || userStore.userInfo.username || '用户' }}</h2>
        <p class="welcome-desc">今天是 {{ today }}，欢迎使用 MyGrade 高校成绩管理系统</p>
      </div>
      <div class="welcome-badge">
        <span class="badge-label">{{ roleLabel }}</span>
      </div>
    </div>

    <div class="stat-grid">
      <div v-for="(stat, i) in stats" :key="i" class="stat-card" :style="{ borderLeft: '4px solid ' + stat.color }">
        <div class="stat-icon-wrap" :style="{ background: stat.bg }">
          <el-icon :size="22" :style="{ color: stat.color }"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="quick-section">
        <h3 class="section-title">快捷入口</h3>
        <div class="quick-grid">
          <div v-for="link in quickLinks" :key="link.path" class="quick-card" @click="$router.push(link.path)">
            <div class="quick-icon" :style="{ background: link.bg }">
              <el-icon :size="20" :style="{ color: link.color }"><component :is="link.icon" /></el-icon>
            </div>
            <div class="quick-text">
              <div class="quick-name">{{ link.name }}</div>
              <div class="quick-desc">{{ link.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="info-section">
        <h3 class="section-title">系统信息</h3>
        <div class="info-list">
          <div class="info-item" v-for="item in systemInfo" :key="item.label">
            <span class="info-label">{{ item.label }}</span>
            <span class="info-value">{{ item.value }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getDashboardStats } from '@/api/dashboard'

const userStore = useUserStore()

const today = computed(() => {
  const d = new Date()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${weekDays[d.getDay()]}`
})

const roleLabel = computed(() => {
  return { admin: '管理员', teacher: '教师', student: '学生' }[userStore.role] || ''
})

const stats = ref([])
const systemInfo = ref([
  { label: '系统版本', value: 'v1.0.0' },
  { label: '技术栈', value: 'Spring Boot 3.2 + Vue 3' },
  { label: '数据库', value: 'MySQL 8.0' },
  { label: '前端框架', value: 'Vue 3 + Element Plus' }
])

const quickLinks = ref([])

if (userStore.role === 'admin') {
  quickLinks.value = [
    { name: '用户管理', desc: '管理系统用户账号', path: '/admin/users', icon: 'User', color: '#FF6900', bg: 'rgba(255,105,0,0.08)' },
    { name: '教师管理', desc: '管理教师信息', path: '/admin/teachers', icon: 'Reading', color: '#409EFF', bg: 'rgba(64,158,255,0.08)' },
    { name: '学生管理', desc: '管理学生信息', path: '/admin/students', icon: 'UserFilled', color: '#67C23A', bg: 'rgba(103,194,58,0.08)' },
    { name: '专业管理', desc: '维护专业信息', path: '/admin/departments', icon: 'OfficeBuilding', color: '#E6A23C', bg: 'rgba(230,162,60,0.08)' },
    { name: '班级管理', desc: '管理班级数据', path: '/admin/classes', icon: 'Collection', color: '#F56C6C', bg: 'rgba(245,108,108,0.08)' },
    { name: '课程管理', desc: '维护课程信息', path: '/admin/courses', icon: 'Reading', color: '#909399', bg: 'rgba(144,147,153,0.08)' }
  ]
} else if (userStore.role === 'teacher') {
  quickLinks.value = [
    { name: '我的班级', desc: '查看所带班级', path: '/teacher/classes', icon: 'UserFilled', color: '#FF6900', bg: 'rgba(255,105,0,0.08)' },
    { name: '成绩录入', desc: '录入学生分数', path: '/teacher/score-input', icon: 'Edit', color: '#409EFF', bg: 'rgba(64,158,255,0.08)' },
    { name: '成绩查询', desc: '查询班级成绩', path: '/teacher/score-query', icon: 'DataAnalysis', color: '#67C23A', bg: 'rgba(103,194,58,0.08)' }
  ]
} else if (userStore.role === 'student') {
  quickLinks.value = [
    { name: '个人信息', desc: '查看个人资料', path: '/student/info', icon: 'User', color: '#FF6900', bg: 'rgba(255,105,0,0.08)' },
    { name: '我的课程', desc: '查看已选课程', path: '/student/courses', icon: 'Reading', color: '#409EFF', bg: 'rgba(64,158,255,0.08)' },
    { name: '我的成绩', desc: '查看各科成绩', path: '/student/scores', icon: 'Trophy', color: '#67C23A', bg: 'rgba(103,194,58,0.08)' }
  ]
}

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    const data = res.data || {}
    if (userStore.role === 'admin') {
      stats.value = [
        { icon: 'User', label: '在校学生', value: data.studentCount || 0, color: '#FF6900', bg: 'rgba(255,105,0,0.08)' },
        { icon: 'Reading', label: '开设课程', value: data.courseCount || 0, color: '#409EFF', bg: 'rgba(64,158,255,0.08)' },
        { icon: 'Trophy', label: '及格率', value: (data.passRate || '0') + '%', color: '#67C23A', bg: 'rgba(103,194,58,0.08)' },
        { icon: 'OfficeBuilding', label: '专业数量', value: data.departmentCount || 0, color: '#E6A23C', bg: 'rgba(230,162,60,0.08)' }
      ]
      systemInfo.value = [
        { label: '学生总数', value: (data.studentCount || 0) + '人' },
        { label: '教师总数', value: (data.teacherCount || 0) + '人' },
        { label: '课程总数', value: (data.courseCount || 0) + '门' },
        { label: '专业数量', value: (data.departmentCount || 0) + '个' },
        { label: '平均成绩', value: data.avgScore || '0.0' },
        { label: '及格率', value: (data.passRate || '0') + '%' }
      ]
    } else if (userStore.role === 'teacher') {
      stats.value = [
        { icon: 'UserFilled', label: '我的学生', value: data.studentCount || 0, color: '#FF6900', bg: 'rgba(255,105,0,0.08)' },
        { icon: 'Reading', label: '我的课程', value: data.courseCount || 0, color: '#409EFF', bg: 'rgba(64,158,255,0.08)' },
        { icon: 'Document', label: '已录成绩', value: data.scoreCount || 0, color: '#E6A23C', bg: 'rgba(230,162,60,0.08)' },
        { icon: 'Trophy', label: '平均分', value: data.avgScore || '0.0', color: '#67C23A', bg: 'rgba(103,194,58,0.08)' }
      ]
    } else {
      stats.value = [
        { icon: 'Document', label: '已修课程', value: data.courseCount || 0, color: '#FF6900', bg: 'rgba(255,105,0,0.08)' },
        { icon: 'Trophy', label: '平均成绩', value: data.avgScore || '0.0', color: '#409EFF', bg: 'rgba(64,158,255,0.08)' },
        { icon: 'Medal', label: '及格率', value: (data.passRate || '0') + '%', color: '#67C23A', bg: 'rgba(103,194,58,0.08)' },
        { icon: 'Calendar', label: '已录成绩', value: data.scoreCount || 0, color: '#E6A23C', bg: 'rgba(230,162,60,0.08)' }
      ]
    }
  } catch (e) { console.error(e) }
}

onMounted(() => fetchStats())
</script>

<style scoped>
.dashboard { min-height: 100%; }

.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 32px;
  background: linear-gradient(135deg, #FF6900 0%, #FF8A3D 50%, #FFB366 100%);
  border-radius: var(--mi-radius-md);
  box-shadow: 0 4px 16px rgba(255,105,0,0.2);
  margin-bottom: 16px;
  color: #fff;
}
.welcome-title { font-size: 22px; font-weight: 700; margin: 0 0 6px; }
.welcome-desc { font-size: 14px; opacity: 0.9; margin: 0; }
.badge-label {
  display: inline-block;
  padding: 8px 24px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255,255,255,0.25);
  color: #fff;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.3);
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}
.stat-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  padding: 22px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: var(--mi-transition);
}
.stat-card:hover {
  box-shadow: var(--mi-shadow-md);
  transform: translateY(-2px);
}
.stat-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-body { flex: 1; min-width: 0; }
.stat-value { font-size: 26px; font-weight: 700; line-height: 1.2; }
.stat-label { font-size: 12px; color: var(--mi-text-secondary); margin-top: 4px; }

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.quick-section, .info-section {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  padding: 22px 24px;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--mi-text-primary);
  margin: 0 0 16px;
  padding-left: 12px;
  border-left: 3px solid var(--mi-primary);
}
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}
.quick-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: var(--mi-radius-sm);
  border: 1px solid var(--mi-border-color);
  cursor: pointer;
  transition: var(--mi-transition);
}
.quick-card:hover {
  border-color: var(--mi-primary);
  box-shadow: 0 2px 8px rgba(255,105,0,0.08);
  transform: translateY(-1px);
}
.quick-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.quick-name { font-size: 14px; font-weight: 600; color: var(--mi-text-primary); }
.quick-desc { font-size: 11px; color: var(--mi-text-secondary); margin-top: 2px; }

.info-list { display: flex; flex-direction: column; gap: 0; }
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--mi-border-color);
}
.info-item:last-child { border-bottom: none; }
.info-label { font-size: 13px; color: var(--mi-text-secondary); }
.info-value { font-size: 13px; font-weight: 500; color: var(--mi-text-primary); }

@media (max-width: 1024px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .content-grid { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .welcome-banner { flex-direction: column; gap: 12px; text-align: center; padding: 20px; }
  .stat-grid { grid-template-columns: 1fr; }
  .quick-grid { grid-template-columns: 1fr; }
}
</style>
