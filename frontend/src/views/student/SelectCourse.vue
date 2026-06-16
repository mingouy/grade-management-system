<template>
  <div class="student-page">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">在线选课</h2>
        <p class="welcome-desc">浏览可选课程，一键选课/退课</p>
      </div>
    </div>

    <div class="tab-bar">
      <div :class="['tab-item', { active: activeTab === 'available' }]" @click="activeTab = 'available'">
        <el-icon><Grid /></el-icon>可选课程
      </div>
      <div :class="['tab-item', { active: activeTab === 'selected' }]" @click="activeTab = 'selected'">
        <el-icon><Document /></el-icon>已选课程 ({{ myCourses.length }})
      </div>
    </div>

    <!-- 可选课程 -->
    <div v-if="activeTab === 'available'" class="page-card">
      <div class="card-header">
        <span class="card-title">可选课程列表</span>
        <div class="filter-bar">
          <el-select v-model="filterSemester" placeholder="全部学期" clearable style="width: 180px" @change="fetchAvailable">
            <el-option label="2024-2025-1" value="2024-2025-1" />
            <el-option label="2024-2025-2" value="2024-2025-2" />
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </div>
      </div>
      <el-table :data="availableCourses" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="courseCode" label="课程代码" width="100" />
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="hours" label="学时" width="70" />
        <el-table-column prop="teacherName" label="授课教师" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleSelect(row)" :disabled="isAlreadySelected(row.id)">
              {{ isAlreadySelected(row.id) ? '已选' : '选课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 已选课程 -->
    <div v-if="activeTab === 'selected'" class="page-card">
      <div class="card-header">
        <span class="card-title">已选课程</span>
        <el-tag type="info" size="small" effect="plain">共 {{ myCourses.length }} 门</el-tag>
      </div>
      <el-table :data="myCourses" v-loading="loading" stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="courseCode" label="课程代码" width="100" />
        <el-table-column prop="credit" label="学分" width="70" />
        <el-table-column prop="teacherName" label="授课教师" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column prop="selectTime" label="选课时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" link @click="handleDrop(row)">退课</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="myCourses.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂未选课，请去可选课程页面选课" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyCourses, getAvailableCourses, selectCourse, dropCourse } from '@/api/courseSelection'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('available')
const loading = ref(false)
const availableCourses = ref([])
const myCourses = ref([])
const filterSemester = ref('')

const fetchAvailable = async () => {
  loading.value = true
  try {
    const params = {}
    if (filterSemester.value) params.semester = filterSemester.value
    const res = await getAvailableCourses(params)
    availableCourses.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const fetchMyCourses = async () => {
  loading.value = true
  try {
    const res = await getMyCourses()
    myCourses.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

const isAlreadySelected = (courseId) => {
  return myCourses.value.some(c => c.courseId === courseId)
}

const handleSelect = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要选修 "${row.courseName}" 吗？`, '确认选课', { type: 'info' })
    await selectCourse({ courseId: row.id })
    ElMessage.success('选课成功')
    fetchMyCourses()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleDrop = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要退选 "${row.courseName}" 吗？`, '确认退课', { type: 'warning' })
    await dropCourse(row.id)
    ElMessage.success('退课成功')
    fetchMyCourses()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

onMounted(() => { fetchAvailable(); fetchMyCourses() })
</script>

<style scoped>
.student-page { display: flex; flex-direction: column; gap: 16px; }
.welcome-banner {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24px 28px; background: linear-gradient(135deg, #FF6900 0%, #FF8A3D 100%);
  border-radius: var(--mi-radius-md); color: #fff;
}
.welcome-title { font-size: 20px; font-weight: 700; margin: 0 0 4px; }
.welcome-desc { font-size: 13px; opacity: 0.85; margin: 0; }
.tab-bar { display: flex; gap: 8px; }
.tab-item {
  display: flex; align-items: center; gap: 6px; padding: 10px 20px;
  border-radius: var(--mi-radius-sm); cursor: pointer; font-size: 14px; font-weight: 500;
  background: var(--mi-bg-card); color: var(--mi-text-regular); transition: var(--mi-transition);
  box-shadow: var(--mi-shadow-sm);
}
.tab-item:hover { color: var(--mi-primary); }
.tab-item.active { background: var(--mi-primary); color: #fff; }
.page-card {
  background: var(--mi-bg-card); border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm); padding: 20px 24px;
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-title { font-size: 16px; font-weight: 600; color: var(--mi-text-primary); }
.filter-bar { display: flex; gap: 8px; }
.empty-tip { padding: 40px 0; }
</style>
