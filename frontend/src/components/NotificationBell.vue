<template>
  <div class="notification-bell" @click="showDropdown = !showDropdown">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
      <el-icon :size="20"><Bell /></el-icon>
    </el-badge>
    <div v-if="showDropdown" class="notification-dropdown">
      <div class="dropdown-header"><span>通知</span><el-button type="primary" link @click.stop="handleMarkAllRead">全部已读</el-button></div>
      <div class="notification-list">
        <div v-if="notifications.length === 0" class="empty">暂无通知</div>
        <div v-for="item in notifications" :key="item.id" :class="['notification-item', { unread: item.isRead === 0 }]" @click.stop="handleRead(item)">
          <div class="notification-title">{{ item.title }}</div>
          <div class="notification-content">{{ item.content }}</div>
          <div class="notification-time">{{ item.createTime }}</div>
        </div>
      </div>
      <div class="dropdown-footer"><el-button type="primary" link @click.stop="goToAll">查看全部</el-button></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { getNotifications, getUnreadCount, markAsRead, markAllAsRead } from '@/api/notification'

const router = useRouter()
const showDropdown = ref(false)
const unreadCount = ref(0)
const notifications = ref([])

const fetchData = async () => {
  try {
    const [countRes, listRes] = await Promise.all([getUnreadCount(), getNotifications({ page: 1, size: 5 })])
    unreadCount.value = countRes.data.count || 0
    notifications.value = listRes.data.records || []
  } catch (e) { console.error(e) }
}

const handleRead = async (item) => {
  if (item.isRead === 0) { await markAsRead(item.id); item.isRead = 1; unreadCount.value = Math.max(0, unreadCount.value - 1) }
}

const handleMarkAllRead = async () => {
  await markAllAsRead(); notifications.value.forEach(n => n.isRead = 1); unreadCount.value = 0
}

const goToAll = () => { showDropdown.value = false; router.push('/notifications') }
const handleClickOutside = (e) => { if (!e.target.closest('.notification-bell')) showDropdown.value = false }

onMounted(() => { fetchData(); document.addEventListener('click', handleClickOutside) })
onUnmounted(() => { document.removeEventListener('click', handleClickOutside) })
</script>

<style scoped>
.notification-bell { position: relative; cursor: pointer; padding: 8px; }
.notification-dropdown { position: absolute; top: 100%; right: 0; width: 320px; background: var(--mi-bg-card); border-radius: var(--mi-radius-sm); box-shadow: var(--mi-shadow-md); z-index: 1000; margin-top: 8px; }
.dropdown-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--mi-border-color); font-weight: 600; }
.notification-list { max-height: 300px; overflow-y: auto; }
.empty { padding: 24px; text-align: center; color: var(--mi-text-secondary); }
.notification-item { padding: 12px 16px; border-bottom: 1px solid var(--mi-border-color); cursor: pointer; }
.notification-item:hover { background: var(--mi-bg-page); }
.notification-item.unread { background: rgba(255,105,0,0.05); }
.notification-title { font-size: 14px; font-weight: 500; margin-bottom: 4px; }
.notification-content { font-size: 12px; color: var(--mi-text-secondary); margin-bottom: 4px; }
.notification-time { font-size: 11px; color: var(--mi-text-placeholder); }
.dropdown-footer { padding: 8px 16px; text-align: center; border-top: 1px solid var(--mi-border-color); }
</style>
