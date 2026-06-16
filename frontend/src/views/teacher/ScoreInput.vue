<template>
  <div class="teacher-page">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">成绩录入</h2>
        <p class="welcome-desc">选择课程，录入或修改学生成绩</p>
      </div>
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="card-title">选择课程</span>
      </div>
      <div class="filter-bar">
        <el-select v-model="selectedCourseId" placeholder="请选择要录入成绩的课程" clearable style="width: 320px" @change="handleCourseChange">
          <el-option v-for="course in courseList" :key="course.id" :label="course.courseName + ' (' + course.courseCode + ')'" :value="course.id" />
        </el-select>
        <el-tag v-if="selectedCourseId" type="success" size="small">已选择课程</el-tag>
      </div>
    </div>

    <div class="page-card" v-if="selectedCourseId">
      <div class="card-header">
        <span class="card-title">学生成绩列表</span>
        <div class="header-actions">
          <el-tag type="info" size="small" effect="plain">共 {{ studentScores.length }} 名学生</el-tag>
        </div>
      </div>

      <el-table :data="studentScores" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="成绩" width="180">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="100" :precision="1" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getGradeType(row.score)" size="small" effect="plain">
              {{ getGradeLabel(row.score) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="submit-area">
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitLoading">
          <el-icon><Check /></el-icon>保存成绩
        </el-button>
      </div>
    </div>

    <div class="page-card empty-state" v-if="!selectedCourseId">
      <el-empty description="请先选择课程">
        <template #image>
          <div class="empty-icon">📚</div>
        </template>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { getScoresByCourse } from '@/api/score'
import { createScore } from '@/api/score'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const courseList = ref([])
const selectedCourseId = ref(null)
const studentScores = ref([])

const fetchCourses = async () => {
  try {
    const res = await request.get('/teacher/courses')
    courseList.value = res.data || []
  } catch (e) { console.error(e) }
}

const getGradeType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 60) return 'warning'
  return 'danger'
}

const getGradeLabel = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 70) return '中等'
  if (score >= 60) return '及格'
  return '不及格'
}

const handleCourseChange = async (courseId) => {
  if (!courseId) { studentScores.value = []; return }
  loading.value = true
  try {
    const res = await getScoresByCourse(courseId)
    studentScores.value = res.data || []
  } catch (e) { studentScores.value = []; console.error(e) } finally { loading.value = false }
}

const handleSubmit = async () => {
  const scores = studentScores.value.map(item => ({
    studentId: item.studentId,
    courseId: selectedCourseId.value,
    score: item.score,
    semester: item.semester || '2024-2025-1',
    examType: '期末考试'
  }))
  submitLoading.value = true
  try { await createScore(scores); ElMessage.success('成绩保存成功') }
  catch (e) { console.error(e) } finally { submitLoading.value = false }
}

onMounted(() => fetchCourses())
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
.filter-bar { display: flex; align-items: center; gap: 12px; }
.header-actions { display: flex; gap: 8px; }
.submit-area { margin-top: 20px; text-align: center; padding-top: 20px; border-top: 1px solid var(--mi-border-color); }
.empty-state { text-align: center; padding: 60px 24px; }
.empty-icon { font-size: 48px; margin-bottom: 16px; }
</style>
