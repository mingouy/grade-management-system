<template>
  <div class="teacher-page">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">成绩查询</h2>
        <p class="welcome-desc">查询所授课程的学生成绩，支持按课程和学期筛选</p>
      </div>
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="card-title">筛选条件</span>
      </div>
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">课程</span>
          <el-select v-model="searchForm.courseId" placeholder="全部课程" clearable style="width: 240px">
            <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </div>
        <div class="filter-item">
          <span class="filter-label">学期</span>
          <el-select v-model="searchForm.semester" placeholder="全部学期" clearable style="width: 180px">
            <el-option label="2024-2025-1" value="2024-2025-1" />
            <el-option label="2024-2025-2" value="2024-2025-2" />
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </div>
        <div class="filter-actions">
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <div class="stat-grid" v-if="scoreList.length > 0">
      <div class="stat-card stat-total">
        <div class="stat-icon-wrap" style="background: rgba(255,105,0,0.08)">
          <el-icon :size="22" style="color: #FF6900"><User /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value" style="color: #FF6900">{{ scoreList.length }}</div>
          <div class="stat-label">总记录数</div>
        </div>
      </div>
      <div class="stat-card stat-avg">
        <div class="stat-icon-wrap" style="background: rgba(64,158,255,0.08)">
          <el-icon :size="22" style="color: #409EFF"><TrendCharts /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value" style="color: #409EFF">{{ averageScore }}</div>
          <div class="stat-label">平均分</div>
        </div>
      </div>
      <div class="stat-card stat-pass">
        <div class="stat-icon-wrap" style="background: rgba(103,194,58,0.08)">
          <el-icon :size="22" style="color: #67C23A"><CircleCheck /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value" style="color: #67C23A">{{ passRate }}%</div>
          <div class="stat-label">及格率</div>
        </div>
      </div>
      <div class="stat-card stat-max">
        <div class="stat-icon-wrap" style="background: rgba(230,162,60,0.08)">
          <el-icon :size="22" style="color: #E6A23C"><Trophy /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value" style="color: #E6A23C">{{ highestScore }}</div>
          <div class="stat-label">最高分</div>
        </div>
      </div>
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="card-title">成绩列表</span>
        <el-tag v-if="scoreList.length > 0" type="info" size="small" effect="plain">共 {{ scoreList.length }} 条记录</el-tag>
      </div>
      <el-table :data="scoreList" v-loading="loading" stripe>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="examType" label="考试类型" width="100" />
        <el-table-column prop="score" label="成绩" width="90">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.score)" size="small" effect="plain">
              {{ row.score }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="scoreList.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无成绩数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getCourseList } from '@/api/course'
import { getScoreList } from '@/api/score'

const loading = ref(false)
const courseList = ref([])
const scoreList = ref([])

const averageScore = computed(() => {
  if (scoreList.value.length === 0) return '0.0'
  const sum = scoreList.value.reduce((acc, item) => acc + (item.score || 0), 0)
  return (sum / scoreList.value.length).toFixed(1)
})
const highestScore = computed(() => {
  if (scoreList.value.length === 0) return '0'
  return Math.max(...scoreList.value.map(item => item.score || 0))
})
const passRate = computed(() => {
  if (scoreList.value.length === 0) return '0.0'
  const passCount = scoreList.value.filter(item => item.score >= 60).length
  return ((passCount / scoreList.value.length) * 100).toFixed(1)
})
const getTagType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

const searchForm = reactive({ courseId: null, semester: '' })

const fetchCourses = async () => {
  try { const res = await getCourseList(); courseList.value = res.data || [] }
  catch (e) { console.error(e) }
}

const handleSearch = async () => {
  loading.value = true
  try { const res = await getScoreList(searchForm); scoreList.value = res.data || [] }
  catch (e) { console.error(e) } finally { loading.value = false }
}

const handleReset = () => { searchForm.courseId = null; searchForm.semester = ''; handleSearch() }

onMounted(() => { fetchCourses(); handleSearch() })
</script>

<style scoped>
.teacher-page { display: flex; flex-direction: column; gap: 16px; }

.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  background: linear-gradient(135deg, #FF6900 0%, #FF8A3D 100%);
  border-radius: var(--mi-radius-md);
  color: #fff;
}
.welcome-title { font-size: 20px; font-weight: 700; margin: 0 0 4px; }
.welcome-desc { font-size: 13px; opacity: 0.85; margin: 0; }

.page-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  padding: 20px 24px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--mi-text-primary);
}
.filter-bar { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
.filter-item { display: flex; align-items: center; gap: 8px; }
.filter-label { font-size: 14px; color: var(--mi-text-regular); font-weight: 500; white-space: nowrap; }
.filter-actions { display: flex; gap: 8px; }

.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: var(--mi-transition);
}
.stat-card:hover { box-shadow: var(--mi-shadow-md); transform: translateY(-1px); }
.stat-icon-wrap { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-body { flex: 1; }
.stat-value { font-size: 24px; font-weight: 700; line-height: 1.2; }
.stat-label { font-size: 12px; color: var(--mi-text-secondary); margin-top: 2px; }

.empty-tip { padding: 40px 0; }

@media (max-width: 768px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
