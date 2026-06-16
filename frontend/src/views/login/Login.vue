<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>

    <div class="login-container">
      <div class="login-header">
        <div class="mi-logo">
          <svg viewBox="0 0 1024 1024" width="36" height="36">
            <path d="M576 384h256v256h-256z" fill="#FF6900"/>
            <path d="M320 128h384v640H320z" fill="#FF6900" opacity="0.15"/>
            <path d="M384 192h256v512H384z" fill="#FF6900" opacity="0.3"/>
            <path d="M448 256h128v384H448z" fill="#FF6900"/>
          </svg>
          <span class="logo-text">MyGrade</span>
        </div>
      </div>

      <div class="login-card">
        <h2 class="card-title">欢迎登录</h2>
        <p class="card-subtitle">高校成绩管理系统</p>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>

          <el-form-item prop="role">
            <div class="role-selector">
              <div
                v-for="opt in roleOptions"
                :key="opt.value"
                :class="['role-option', { active: loginForm.role === opt.value }]"
                @click="loginForm.role = opt.value"
              >
                <el-icon :size="18"><component :is="opt.icon" /></el-icon>
                <span>{{ opt.label }}</span>
              </div>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-hint">
          <span>测试账号: admin / teacher01 / 2023010101</span>
          <span>密码统一: 123456</span>
        </div>
      </div>

      <div class="login-footer">
        <p>MyGrade 高校成绩管理系统 &copy; 2024</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, Setting, Reading, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  role: ''
})

const roleOptions = [
  { label: '管理员', value: 'admin', icon: Setting },
  { label: '教师', value: 'teacher', icon: Reading },
  { label: '学生', value: 'student', icon: UserFilled }
]

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.userInfo)
    ElMessage.success('登录成功')

    const role = res.data.userInfo.role
    if (role === 'admin') {
      router.push('/admin/users')
    } else if (role === 'teacher') {
      router.push('/teacher/classes')
    } else if (role === 'student') {
      router.push('/student/info')
    } else {
      router.push('/dashboard')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.12;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: var(--mi-primary);
  top: -200px;
  right: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: var(--mi-primary);
  bottom: -150px;
  left: -100px;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: var(--mi-primary-light);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 24px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.mi-logo {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.logo-text {
  font-size: 26px;
  font-weight: 700;
  color: var(--mi-text-primary);
  letter-spacing: -0.5px;
}

.login-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-lg);
  padding: 40px 36px 32px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--mi-text-primary);
  margin: 0;
  letter-spacing: -0.3px;
}

.card-subtitle {
  font-size: 13px;
  color: var(--mi-text-secondary);
  margin: 6px 0 28px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
  margin-top: 8px;
}

.login-form :deep(.el-input__wrapper) {
  border: 1px solid var(--mi-border-color);
  border-radius: var(--mi-radius-sm);
  box-shadow: none !important;
  padding: 4px 12px;
  height: 44px;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #ccc;
  box-shadow: none !important;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mi-primary);
  box-shadow: 0 0 0 2px rgba(255, 105, 0, 0.08) !important;
}

.login-form :deep(.el-input__prefix .el-icon) {
  color: var(--mi-text-secondary);
}

.role-selector {
  display: flex;
  gap: 12px;
  width: 100%;
}

.role-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 0;
  border: 1.5px solid var(--mi-border-color);
  border-radius: var(--mi-radius-sm);
  cursor: pointer;
  transition: var(--mi-transition);
  font-size: 13px;
  font-weight: 500;
  color: var(--mi-text-regular);
  background: #fff;
}

.role-option:hover {
  border-color: var(--mi-primary);
  color: var(--mi-primary);
}

.role-option.active {
  border-color: var(--mi-primary);
  background: var(--mi-primary-lighter);
  color: var(--mi-primary);
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: var(--mi-radius-sm) !important;
  letter-spacing: 2px;
}

.login-hint {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: center;
}

.login-hint span {
  font-size: 12px;
  color: var(--mi-text-placeholder);
}

.login-footer {
  margin-top: 36px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #ccc;
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px 24px;
  }

  .logo-text {
    font-size: 22px;
  }

  .role-selector {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
