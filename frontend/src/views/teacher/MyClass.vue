<template>
  <div class="teacher-page">
    <div class="page-card">
      <div class="card-header">
        <span class="card-title">我的班级</span>
        <el-tag type="info" size="small" effect="plain">共 {{ classList.length }} 个班级</el-tag>
      </div>
      <el-table :data="classList" v-loading="loading" stripe>
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="classCode" label="班级编码" />
        <el-table-column prop="gradeYear" label="年级" width="80" />
        <el-table-column prop="deptName" label="所属专业" />
        <el-table-column prop="studentCount" label="学生人数" width="90" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewStudents(row)">查看学生</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="studentDialogVisible" :title="currentClassName + ' - 学生列表'" width="720px" destroy-on-close>
      <el-table :data="studentList" v-loading="studentLoading" stripe>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const classList = ref([])
const studentDialogVisible = ref(false)
const currentClassName = ref('')
const studentList = ref([])
const studentLoading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/teacher/classes')
    classList.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const viewStudents = async (row) => {
  currentClassName.value = row.className
  studentDialogVisible.value = true
  studentLoading.value = true
  try {
    const res = await request.get(`/teacher/classes/${row.id}/students`)
    studentList.value = res.data || []
  } catch (e) { console.error(e) } finally { studentLoading.value = false }
}

onMounted(() => fetchData())
</script>

<style scoped>
.teacher-page { display: flex; flex-direction: column; gap: 16px; }
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
</style>
