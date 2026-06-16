<template>
  <div class="manage-page">
    <div class="page-card">
      <div class="card-header">
        <span class="card-title">学生信息管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增学生
        </el-button>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-bar">
        <el-form-item label="所属专业">
          <el-select v-model="searchForm.deptId" placeholder="全部专业" clearable style="width: 160px" @change="handleDeptChange">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属班级">
          <el-select v-model="searchForm.classId" placeholder="全部班级" clearable style="width: 160px">
            <el-option v-for="cls in filteredClassList" :key="cls.id" :label="cls.className" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="page-card">
      <el-table :data="studentList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="deptName" label="所属专业" />
        <el-table-column prop="className" label="所属班级" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="90" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
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
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入学生姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属专业" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择专业" style="width: 100%" @change="handleFormDeptChange">
            <el-option v-for="dept in departmentList" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属班级" prop="classId">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
            <el-option v-for="cls in formClassList" :key="cls.id" :label="cls.className" :value="cls.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入学年份" prop="enrollmentYear">
          <el-input-number v-model="form.enrollmentYear" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { getStudentInfoList, createStudentInfo, updateStudentInfo, deleteStudentInfo } from '@/api/studentInfo'
import { getDepartmentList } from '@/api/department'
import { getClassList } from '@/api/classInfo'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const studentList = ref([])
const departmentList = ref([])
const allClassList = ref([])
const searchForm = reactive({ deptId: null, classId: null })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref()
const form = reactive({ id: null, studentNo: '', name: '', gender: '男', deptId: null, classId: null, enrollmentYear: new Date().getFullYear(), phone: '', email: '' })
const formRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择所属专业', trigger: 'change' }],
  classId: [{ required: true, message: '请选择所属班级', trigger: 'change' }],
  enrollmentYear: [{ required: true, message: '请输入入学年份', trigger: 'blur' }]
}

const filteredClassList = computed(() => {
  if (!searchForm.deptId) return allClassList.value
  return allClassList.value.filter(c => c.deptId === searchForm.deptId)
})

const formClassList = computed(() => {
  if (!form.deptId) return allClassList.value
  return allClassList.value.filter(c => c.deptId === form.deptId)
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.classId) params.classId = searchForm.classId
    else if (searchForm.deptId) params.deptId = searchForm.deptId
    const res = await getStudentInfoList(params)
    studentList.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const fetchDepartments = async () => {
  try { const res = await getDepartmentList(); departmentList.value = res.data || [] }
  catch (e) { console.error(e) }
}

const fetchClasses = async () => {
  try { const res = await getClassList(); allClassList.value = res.data || [] }
  catch (e) { console.error(e) }
}

const handleDeptChange = () => { searchForm.classId = null }
const handleFormDeptChange = () => { form.classId = null }
const handleSearch = () => fetchData()
const handleReset = () => { searchForm.deptId = null; searchForm.classId = null; fetchData() }

const resetForm = () => { Object.assign(form, { id: null, studentNo: '', name: '', gender: '男', deptId: null, classId: null, enrollmentYear: new Date().getFullYear(), phone: '', email: '' }) }
const handleAdd = () => { resetForm(); dialogTitle.value = '新增学生'; dialogVisible.value = true }
const handleEdit = (row) => { resetForm(); Object.assign(form, row); dialogTitle.value = '编辑学生'; dialogVisible.value = true }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.id) { await updateStudentInfo(form.id, form); ElMessage.success('更新成功') }
    else { await createStudentInfo(form); ElMessage.success('创建成功') }
    dialogVisible.value = false; fetchData()
  } catch (e) { console.error(e) } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生 "${row.name}" 吗？`, '提示', { type: 'warning' })
    .then(async () => { await deleteStudentInfo(row.id); ElMessage.success('删除成功'); fetchData() }).catch(() => {})
}

onMounted(() => { fetchData(); fetchDepartments(); fetchClasses() })
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

.search-bar {
  margin: 0;
}
</style>
