<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <span class="card-title">专业管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增专业
        </el-button>
      </div>

      <el-table :data="departmentList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="deptName" label="专业名称" />
        <el-table-column prop="deptCode" label="专业代码" />
        <el-table-column prop="description" label="专业描述" show-overflow-tooltip />
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
        <el-form-item label="专业名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业代码" prop="deptCode">
          <el-input v-model="form.deptCode" placeholder="请输入专业代码" />
        </el-form-item>
        <el-form-item label="专业描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入专业描述" />
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
import { getDepartmentList, createDepartment, updateDepartment, deleteDepartment } from '@/api/department'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const departmentList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref()
const form = reactive({ id: null, deptName: '', deptCode: '', description: '' })
const formRules = {
  deptName: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入专业代码', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try { const res = await getDepartmentList(); departmentList.value = res.data || [] }
  catch (e) { console.error(e) } finally { loading.value = false }
}

const resetForm = () => { Object.assign(form, { id: null, deptName: '', deptCode: '', description: '' }) }
const handleAdd = () => { resetForm(); dialogTitle.value = '新增专业'; dialogVisible.value = true }
const handleEdit = (row) => { resetForm(); Object.assign(form, row); dialogTitle.value = '编辑专业'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.id) { await updateDepartment(form.id, form); ElMessage.success('更新成功') }
    else { await createDepartment(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { console.error(e) } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除专业 "${row.deptName}" 吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteDepartment(row.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
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
</style>
