<template>
  <div class="info-page">
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-wrap">
          <div class="avatar">{{ studentInfo.name ? studentInfo.name.charAt(0) : '?' }}</div>
        </div>
        <div class="user-meta">
          <h2 class="user-name">{{ studentInfo.name || '未知用户' }}</h2>
          <div class="tags">
            <el-tag type="primary" size="small">学生</el-tag>
          </div>
        </div>
      </div>

      <div class="info-grid">
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-row" v-for="item in basicInfo" :key="item.label">
            <span class="info-label">{{ item.label }}</span>
            <span class="info-value">{{ item.value }}</span>
          </div>
        </div>

        <div class="info-section">
          <h3 class="section-title">学籍信息</h3>
          <div class="info-row" v-for="item in academicInfo" :key="item.label">
            <span class="info-label">{{ item.label }}</span>
            <span class="info-value">{{ item.value }}</span>
          </div>
        </div>

        <div class="info-section">
          <h3 class="section-title">联系方式</h3>
          <div class="info-row" v-for="item in contactInfo" :key="item.label">
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
import { getStudentInfo } from '@/api/student'

const studentInfo = ref({})

const basicInfo = computed(() => [
  { label: '学号', value: studentInfo.value.studentNo || '-' },
  { label: '姓名', value: studentInfo.value.name || '-' },
  { label: '性别', value: studentInfo.value.gender || '-' }
])

const academicInfo = computed(() => [
  { label: '专业', value: studentInfo.value.deptName || '-' },
  { label: '班级', value: studentInfo.value.className || '-' },
  { label: '入学年份', value: studentInfo.value.enrollmentYear || '-' }
])

const contactInfo = computed(() => [
  { label: '手机号', value: studentInfo.value.phone || '-' },
  { label: '邮箱', value: studentInfo.value.email || '-' }
])

const fetchData = async () => {
  try { const res = await getStudentInfo(); studentInfo.value = res.data || {} }
  catch (e) { console.error(e) }
}

onMounted(() => fetchData())
</script>

<style scoped>
.info-page {
  max-width: 900px;
}

.profile-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  overflow: hidden;
}

.profile-header {
  background: linear-gradient(135deg, var(--mi-primary), var(--mi-primary-light));
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: #fff;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  border: 3px solid rgba(255, 255, 255, 0.3);
  flex-shrink: 0;
}

.user-name {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
}

.tags {
  display: flex;
  gap: 8px;
}

.info-grid {
  padding: 24px 32px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
}

.info-section {
  background: var(--mi-bg-page);
  border-radius: var(--mi-radius-sm);
  padding: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--mi-text-primary);
  margin: 0 0 16px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--mi-primary);
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--mi-border-color);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: var(--mi-text-secondary);
}

.info-value {
  font-size: 13px;
  font-weight: 500;
  color: var(--mi-text-primary);
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
    padding: 24px;
  }

  .info-grid {
    padding: 16px;
    gap: 16px;
  }
}
</style>
