<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <span class="card-title">班级管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增班级
        </el-button>
      </div>

      <el-table :data="classList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="classCode" label="班级编码" />
        <el-table-column prop="gradeYear" label="年级" width="80" />
        <el-table-column prop="deptName" label="所属专业" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="班级编码" prop="classCode">
          <el-input v-model="form.classCode" placeholder="请输入班级编码" />
        </el-form-item>
        <el-form-item label="年级" prop="gradeYear">
          <el-input-number v-model="form.gradeYear" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item label="所属专业" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择专业" style="width: 100%">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
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
import { ref, reactive, onMounted } from 'vue'
import { getClassList, createClass, updateClass, deleteClass } from '@/api/classInfo'
import { getDepartmentList } from '@/api/department'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const classList = ref([])
const departmentList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref()
const form = reactive({ id: null, className: '', classCode: '', gradeYear: new Date().getFullYear(), deptId: null })
const formRules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  classCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  gradeYear: [{ required: true, message: '请输入年级', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属专业', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try { const res = await getClassList(); classList.value = res.data || [] }
  catch (e) { console.error(e) } finally { loading.value = false }
}

const fetchDepartments = async () => {
  try { const res = await getDepartmentList(); departmentList.value = res.data || [] }
  catch (e) { console.error(e) }
}

const resetForm = () => { Object.assign(form, { id: null, className: '', classCode: '', gradeYear: new Date().getFullYear(), deptId: null }) }
const handleAdd = () => { resetForm(); dialogTitle.value = '新增班级'; dialogVisible.value = true }
const handleEdit = (row) => { resetForm(); Object.assign(form, row); dialogTitle.value = '编辑班级'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.id) { await updateClass(form.id, form); ElMessage.success('更新成功') }
    else { await createClass(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { console.error(e) } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除班级 "${row.className}" 吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteClass(row.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
}

onMounted(() => { fetchData(); fetchDepartments() })
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
</style>
