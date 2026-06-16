<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <span class="card-title">我的课程</span>
        <el-tag type="info" size="small" effect="plain">共 {{ courseList.length }} 门课程</el-tag>
      </div>

      <el-table :data="courseList" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="courseName" label="课程名称">
          <template #default="{ row }">
            <span class="course-name-cell">{{ row.courseName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="courseCode" label="课程代码" />
        <el-table-column prop="credit" label="学分" width="70">
          <template #default="{ row }">
            <el-tag type="primary" size="small" effect="plain">{{ row.credit }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="授课教师" />
        <el-table-column prop="semester" label="学期" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentCourses } from '@/api/student'

const loading = ref(false)
const courseList = ref([])

const fetchData = async () => {
  loading.value = true
  try { const res = await getStudentCourses(); courseList.value = res.data || [] }
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

.course-name-cell {
  font-weight: 600;
  color: var(--mi-text-primary);
}
</style>
