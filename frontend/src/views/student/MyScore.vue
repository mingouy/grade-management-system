<template>
  <div class="manage-page">
    <div class="summary-cards">
      <div class="summary-card">
        <div class="summary-value" style="color: var(--mi-primary)">{{ totalCredits }}</div>
        <div class="summary-label">总学分</div>
      </div>
      <div class="summary-card">
        <div class="summary-value" style="color: #409EFF">{{ averageScore }}</div>
        <div class="summary-label">平均分</div>
      </div>
      <div class="summary-card">
        <div class="summary-value" style="color: #67C23A">{{ gpa }}</div>
        <div class="summary-label">GPA</div>
      </div>
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="card-title">成绩明细</span>
      </div>

      <el-table :data="scoreList" v-loading="loading" stripe>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="courseCode" label="课程代码" />
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="semester" label="学期" />
        <el-table-column prop="examType" label="考试类型" width="100" />
        <el-table-column prop="score" label="成绩" width="90">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'" size="small" effect="plain">
              {{ row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="70">
          <template #default="{ row }">
            <span :style="{ color: getGradeColor(row.score), fontWeight: 600 }">
              {{ getGradeLevel(row.score) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStudentScores } from '@/api/student'

const loading = ref(false)
const scoreList = ref([])

const totalCredits = computed(() => {
  return scoreList.value.reduce((sum, item) => sum + (item.credit || 0), 0)
})

const averageScore = computed(() => {
  if (scoreList.value.length === 0) return '0.0'
  const sum = scoreList.value.reduce((acc, item) => acc + (item.score || 0), 0)
  return (sum / scoreList.value.length).toFixed(1)
})

const gpa = computed(() => {
  if (scoreList.value.length === 0) return '0.00'
  let totalPoints = 0, totalCreditsVal = 0
  scoreList.value.forEach(item => {
    const credit = item.credit || 0
    const score = item.score || 0
    totalPoints += getGradePoint(score) * credit
    totalCreditsVal += credit
  })
  return totalCreditsVal === 0 ? '0.00' : (totalPoints / totalCreditsVal).toFixed(2)
})

const getGradePoint = (score) => {
  if (score >= 90) return 4.0
  if (score >= 85) return 3.7
  if (score >= 82) return 3.3
  if (score >= 78) return 3.0
  if (score >= 75) return 2.7
  if (score >= 72) return 2.3
  if (score >= 68) return 2.0
  if (score >= 64) return 1.5
  if (score >= 60) return 1.0
  return 0.0
}

const getGradeLevel = (score) => {
  if (score >= 90) return '优'
  if (score >= 80) return '良'
  if (score >= 70) return '中'
  if (score >= 60) return '及格'
  return '不及格'
}

const getGradeColor = (score) => {
  if (score >= 90) return '#67C23A'
  if (score >= 80) return '#409EFF'
  if (score >= 70) return '#E6A23C'
  if (score >= 60) return '#909399'
  return '#F56C6C'
}

const fetchData = async () => {
  loading.value = true
  try { const res = await getStudentScores(); scoreList.value = res.data || [] }
  catch (e) { console.error(e) } finally { loading.value = false }
}

onMounted(() => fetchData())
</script>

<style scoped>
.manage-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.summary-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  padding: 24px;
  text-align: center;
}

.summary-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 4px;
}

.summary-label {
  font-size: 13px;
  color: var(--mi-text-secondary);
}

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

@media (max-width: 768px) {
  .summary-cards {
    grid-template-columns: 1fr;
  }
}
</style>
