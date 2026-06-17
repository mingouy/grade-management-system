<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar">{{ userInfo.realName ? userInfo.realName.charAt(0) : '?' }}</div>
        <div class="user-meta">
          <h2 class="user-name">{{ userInfo.realName || '未知用户' }}</h2>
          <el-tag type="primary" size="small">{{ roleLabel }}</el-tag>
        </div>
      </div>
      <div class="profile-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人信息" name="info">
            <el-form :model="profileForm" label-width="80px" class="profile-form">
              <el-form-item label="用户名"><el-input v-model="userInfo.username" disabled /></el-form-item>
              <el-form-item label="姓名"><el-input v-model="profileForm.realName" /></el-form-item>
              <el-form-item label="手机号"><el-input v-model="profileForm.phone" /></el-form-item>
              <el-form-item label="邮箱"><el-input v-model="profileForm.email" /></el-form-item>
              <el-form-item><el-button type="primary" @click="handleUpdateProfile">保存修改</el-button></el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" label-width="100px" class="profile-form">
              <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
              <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
              <el-form-item label="确认新密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
              <el-form-item><el-button type="primary" @click="handleChangePassword">修改密码</el-button></el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getProfile, updateProfile, changePassword } from '@/api/profile'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')
const userInfo = ref({})
const roleLabel = computed(() => ({ admin: '管理员', teacher: '教师', student: '学生' }[userStore.role] || ''))
const profileForm = reactive({ realName: '', phone: '', email: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const fetchProfile = async () => {
  try {
    const res = await getProfile()
    userInfo.value = res.data || {}
    profileForm.realName = userInfo.value.realName || ''
    profileForm.phone = userInfo.value.phone || ''
    profileForm.email = userInfo.value.email || ''
  } catch (e) { console.error(e) }
}

const handleUpdateProfile = async () => {
  try {
    await updateProfile(profileForm)
    ElMessage.success('保存成功')
    userStore.setUserInfo({ ...userStore.userInfo, ...profileForm })
  } catch (e) { console.error(e) }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) { ElMessage.error('两次密码不一致'); return }
  if (passwordForm.newPassword.length < 6) { ElMessage.error('密码长度不能少于6位'); return }
  try {
    await changePassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''; passwordForm.newPassword = ''; passwordForm.confirmPassword = ''
  } catch (e) { console.error(e) }
}

onMounted(() => fetchProfile())
</script>

<style scoped>
.profile-page { max-width: 800px; }
.profile-card { background: var(--mi-bg-card); border-radius: var(--mi-radius-md); box-shadow: var(--mi-shadow-sm); overflow: hidden; }
.profile-header { background: linear-gradient(135deg, var(--mi-primary), var(--mi-primary-light)); padding: 32px; display: flex; align-items: center; gap: 20px; color: #fff; }
.avatar { width: 72px; height: 72px; border-radius: 50%; background: rgba(255,255,255,0.2); display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: 700; border: 3px solid rgba(255,255,255,0.3); }
.user-name { font-size: 22px; font-weight: 700; margin: 0 0 8px; }
.profile-content { padding: 24px 32px; }
.profile-form { max-width: 400px; margin-top: 16px; }
</style>
