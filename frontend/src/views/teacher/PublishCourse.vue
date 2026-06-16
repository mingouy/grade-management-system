<template>
  <div class="teacher-page">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">发布课程</h2>
        <p class="welcome-desc">创建新课程或管理已发布的课程</p>
      </div>
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="card-title">我的课程</span>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>发布新课程</el-button>
      </div>
      <el-table :data="courseList" v-loading="loading" stripe>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="courseCode" label="课程代码" width="100" />
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="hours" label="学时" width="70" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="0.5" :max="10" :step="0.5" />
        </el-form-item>
        <el-form-item label="学时" prop="hours">
          <el-input-number v-model="form.hours" :min="8" :max="200" :step="8" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="form.semester" placeholder="请选择学期" style="width: 100%">
            <el-option label="2024-2025-1" value="2024-2025-1" />
            <el-option label="2024-2025-2" value="2024-2025-2" />
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const courseList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref({ id: null, courseName: '', courseCode: '', credit: 2, hours: 32, semester: '2024-2025-1' })
const formRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseCode: [{ required: true, message: '请输入课程代码', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  hours: [{ required: true, message: '请输入学时', trigger: 'blur' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/teacher/courses')
    courseList.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const resetForm = () => { form.value = { id: null, courseName: '', courseCode: '', credit: 2, hours: 32, semester: '2024-2025-1' } }
const handleAdd = () => { resetForm(); dialogTitle.value = '发布新课程'; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogTitle.value = '编辑课程'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.value.id) {
      await request.put(`/teacher/update-course/${form.value.id}`, form.value)
      ElMessage.success('课程更新成功')
    } else {
      await request.post('/teacher/publish-course', form.value)
      ElMessage.success('课程发布成功')
    }
    dialogVisible.value = false; fetchData()
  } catch (e) { console.error(e) } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除课程 "${row.courseName}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await request.delete(`/teacher/delete-course/${row.id}`)
      ElMessage.success('删除成功'); fetchData()
    }).catch(() => {})
}

onMounted(() => fetchData())
</script>

<style scoped>
.teacher-page { display: flex; flex-direction: column; gap: 16px; }
.welcome-banner {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24px 28px; background: linear-gradient(135deg, #FF6900 0%, #FF8A3D 100%);
  border-radius: var(--mi-radius-md); color: #fff;
}
.welcome-title { font-size: 20px; font-weight: 700; margin: 0 0 4px; }
.welcome-desc { font-size: 13px; opacity: 0.85; margin: 0; }
.page-card {
  background: var(--mi-bg-card); border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm); padding: 20px 24px;
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-title { font-size: 16px; font-weight: 600; color: var(--mi-text-primary); }
</style>
