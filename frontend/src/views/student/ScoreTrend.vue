<template>
  <div class="analysis-page">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">成绩趋势</h2>
        <p class="welcome-desc">查看各学期成绩变化趋势</p>
      </div>
    </div>

    <div class="chart-card">
      <h3 class="chart-title">学期成绩趋势</h3>
      <v-chart :option="trendOption" style="height:400px" autoresize />
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="card-title">学期明细</span>
      </div>
      <el-table :data="trendData" v-loading="loading" stripe>
        <el-table-column prop="semester" label="学期" width="150" />
        <el-table-column prop="courseCount" label="课程数" width="80" />
        <el-table-column prop="avgScore" label="平均分" width="80" />
        <el-table-column prop="passRate" label="及格率(%)" width="100">
          <template #default="{ row }">
            <el-tag :type="parseFloat(row.passRate) >= 80 ? 'success' : 'warning'" size="small">{{ row.passRate }}%</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && trendData.length === 0" description="暂无成绩数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getStudentScoreTrend } from '@/api/student'

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const loading = ref(false)
const trendData = ref([])

const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['平均分', '及格率'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: trendData.value.map(d => d.semester),
    axisLabel: { rotate: 30 }
  },
  yAxis: [
    { type: 'value', name: '分数', min: 0, max: 100 },
    { type: 'value', name: '百分比(%)', min: 0, max: 100 }
  ],
  series: [
    {
      name: '平均分',
      type: 'line',
      data: trendData.value.map(d => parseFloat(d.avgScore)),
      itemStyle: { color: '#FF6900' },
      smooth: true,
      areaStyle: { color: 'rgba(255,105,0,0.1)' }
    },
    {
      name: '及格率',
      type: 'line',
      yAxisIndex: 1,
      data: trendData.value.map(d => parseFloat(d.passRate)),
      itemStyle: { color: '#67C23A' },
      smooth: true,
      areaStyle: { color: 'rgba(103,194,58,0.1)' }
    }
  ]
}))

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStudentScoreTrend()
    trendData.value = res.data || []
  } catch (e) { console.error(e) } finally { loading.value = false }
}

onMounted(() => fetchData())
</script>

<style scoped>
.analysis-page { display: flex; flex-direction: column; gap: 16px; }
.welcome-banner { padding: 24px 28px; background: linear-gradient(135deg, #FF6900 0%, #FF8A3D 100%); border-radius: var(--mi-radius-md); color: #fff; }
.welcome-title { font-size: 20px; font-weight: 700; margin: 0 0 4px; }
.welcome-desc { font-size: 13px; opacity: 0.85; margin: 0; }
.chart-card, .page-card { background: var(--mi-bg-card); border-radius: var(--mi-radius-md); padding: 20px 24px; box-shadow: var(--mi-shadow-sm); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-title { font-size: 16px; font-weight: 600; color: var(--mi-text-primary); }
.chart-title { font-size: 16px; font-weight: 600; margin: 0 0 16px; color: var(--mi-text-primary); }
</style>
