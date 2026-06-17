<template>
  <div class="notification-page">
    <div class="page-header"><h2>通知中心</h2><el-button type="primary" @click="handleMarkAllRead">全部已读</el-button></div>
    <div class="notification-list">
      <div v-if="notifications.length === 0" class="empty">暂无通知</div>
      <div v-for="item in notifications" :key="item.id" :class="['notification-card', { unread: item.isRead === 0 }]">
        <div class="card-header"><span class="title">{{ item.title }}</span><span class="time">{{ item.createTime }}</span></div>
        <div class="card-body">{{ item.content }}</div>
      </div>
    </div>
    <el-pagination v-if="total > 0" layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="fetchData" style="margin-top:16px;justify-content:center" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotifications, markAllAsRead } from '@/api/notification'
import { ElMessage } from 'element-plus'

const notifications = ref([])
const total = ref(0)
const currentPage = ref(1)

const fetchData = async () => {
  try {
    const res = await getNotifications({ page: currentPage.value, size: 10 })
    notifications.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
}

const handleMarkAllRead = async () => {
  await markAllAsRead(); ElMessage.success('已全部标记为已读'); fetchData()
}

onMounted(() => fetchData())
</script>

<style scoped>
.notification-page { max-width: 800px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
.notification-card { background: var(--mi-bg-card); border-radius: var(--mi-radius-sm); padding: 16px; margin-bottom: 12px; border-left: 3px solid var(--mi-primary); }
.notification-card.unread { border-left-color: #FF6900; background: rgba(255,105,0,0.02); }
.card-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.title { font-weight: 600; }
.time { font-size: 12px; color: var(--mi-text-secondary); }
.card-body { font-size: 14px; color: var(--mi-text-regular); }
.empty { text-align: center; padding: 40px; color: var(--mi-text-secondary); }
</style>
