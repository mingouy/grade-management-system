<template>
  <div class="analysis-page">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2 class="welcome-title">成绩分析</h2>
        <p class="welcome-desc">查看成绩分布、排名和挂科预警</p>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="成绩分布" name="distribution">
        <div class="chart-row">
          <div class="chart-card">
            <h3 class="chart-title">成绩分布</h3>
            <v-chart :option="pieOption" style="height:350px" autoresize />
          </div>
          <div class="chart-card">
            <h3 class="chart-title">关键指标</h3>
            <div class="metric-grid">
              <div class="metric-item">
                <div class="metric-value" style="color:#FF6900">{{ distribution.avgScore || '0.0' }}</div>
                <div class="metric-label">平均分</div>
              </div>
              <div class="metric-item">
                <div class="metric-value" style="color:#67C23A">{{ distribution.passRate || '0.0' }}%</div>
                <div class="metric-label">及格率</div>
              </div>
              <div class="metric-item">
                <div class="metric-value" style="color:#409EFF">{{ distribution.total || 0 }}</div>
                <div class="metric-label">总人数</div>
              </div>
              <div class="metric-item">
                <div class="metric-value" style="color:#F56C6C">{{ distribution.fail || 0 }}</div>
                <div class="metric-label">不及格</div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="GPA排名" name="ranking">
        <div class="chart-card">
          <h3 class="chart-title">GPA Top 10</h3>
          <v-chart :option="barOption" style="height:400px" autoresize />
        </div>
        <div class="table-card">
          <el-table :data="ranking" stripe max-height="400">
            <el-table-column type="index" label="排名" width="70" />
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column prop="studentName" label="姓名" />
            <el-table-column prop="gpa" label="GPA" width="80">
              <template #default="{ row }"><span style="font-weight:700;color:#FF6900">{{ row.gpa }}</span></template>
            </el-table-column>
            <el-table-column prop="avgScore" label="平均分" width="80" />
            <el-table-column prop="courseCount" label="课程数" width="80" />
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="挂科预警" name="failing">
        <div class="chart-card" v-if="failingList.length > 0">
          <h3 class="chart-title">挂科人数统计</h3>
          <v-chart :option="failingBarOption" style="height:300px" autoresize />
        </div>
        <div class="table-card">
          <el-table :data="failingList" stripe max-height="400">
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column prop="studentName" label="姓名" width="100" />
            <el-table-column prop="failCount" label="挂科数" width="80">
              <template #default="{ row }"><el-tag type="danger" size="small">{{ row.failCount }}</el-tag></template>
            </el-table-column>
            <el-table-column label="挂科课程">
              <template #default="{ row }">
                <div v-for="c in row.courses" :key="c.courseName" style="margin:2px 0">
                  <el-tag type="warning" size="small" effect="plain">{{ c.courseName }}: {{ c.score }}</el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="failingList.length === 0" description="暂无挂科学生" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="学期对比" name="semester">
        <div class="chart-card">
          <h3 class="chart-title">学期成绩对比</h3>
          <v-chart :option="semesterLineOption" style="height:400px" autoresize />
        </div>
        <div class="table-card">
          <el-table :data="semesterData" stripe max-height="400">
            <el-table-column prop="semester" label="学期" width="150" />
            <el-table-column prop="count" label="成绩数" width="80" />
            <el-table-column prop="avgScore" label="平均分" width="80" />
            <el-table-column prop="passRate" label="及格率(%)" width="100" />
            <el-table-column prop="excellentRate" label="优秀率(%)" width="100" />
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getGradeDistribution, getGradeRanking, getFailingStudents, getSemesterComparison } from '@/api/analysis'

use([CanvasRenderer, PieChart, BarChart, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const activeTab = ref('distribution')
const distribution = ref({})
const ranking = ref([])
const failingList = ref([])
const semesterData = ref([])

const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{
    name: '成绩分布',
    type: 'pie',
    radius: ['40%', '70%'],
    avoidLabelOverlap: false,
    itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
    label: { show: false, position: 'center' },
    emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
    labelLine: { show: false },
    data: [
      { value: distribution.value.excellent || 0, name: '优秀(90+)', itemStyle: { color: '#67C23A' } },
      { value: distribution.value.good || 0, name: '良好(80-89)', itemStyle: { color: '#409EFF' } },
      { value: distribution.value.medium || 0, name: '中等(70-79)', itemStyle: { color: '#E6A23C' } },
      { value: distribution.value.pass || 0, name: '及格(60-69)', itemStyle: { color: '#909399' } },
      { value: distribution.value.fail || 0, name: '不及格(<60)', itemStyle: { color: '#F56C6C' } }
    ]
  }]
}))

const barOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: ranking.value.slice(0, 10).map(r => r.studentName),
    axisLabel: { rotate: 30 }
  },
  yAxis: { type: 'value', min: 0, max: 4.0 },
  series: [{
    name: 'GPA',
    type: 'bar',
    data: ranking.value.slice(0, 10).map(r => ({
      value: parseFloat(r.gpa),
      itemStyle: { color: '#FF6900' }
    })),
    barWidth: '50%'
  }]
}))

const failingBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: failingList.value.slice(0, 10).map(r => r.studentName),
    axisLabel: { rotate: 30 }
  },
  yAxis: { type: 'value', min: 0 },
  series: [{
    name: '挂科数',
    type: 'bar',
    data: failingList.value.slice(0, 10).map(r => ({
      value: r.failCount,
      itemStyle: { color: '#F56C6C' }
    })),
    barWidth: '50%'
  }]
}))

const semesterLineOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['平均分', '及格率', '优秀率'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: semesterData.value.map(d => d.semester),
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
      data: semesterData.value.map(d => parseFloat(d.avgScore)),
      itemStyle: { color: '#FF6900' },
      smooth: true
    },
    {
      name: '及格率',
      type: 'line',
      yAxisIndex: 1,
      data: semesterData.value.map(d => parseFloat(d.passRate)),
      itemStyle: { color: '#67C23A' },
      smooth: true
    },
    {
      name: '优秀率',
      type: 'line',
      yAxisIndex: 1,
      data: semesterData.value.map(d => parseFloat(d.excellentRate)),
      itemStyle: { color: '#409EFF' },
      smooth: true
    }
  ]
}))

const fetchData = async () => {
  try {
    const [distRes, rankRes, failRes, semRes] = await Promise.all([
      getGradeDistribution(),
      getGradeRanking(),
      getFailingStudents(),
      getSemesterComparison()
    ])
    distribution.value = distRes.data || {}
    ranking.value = rankRes.data || []
    failingList.value = failRes.data || []
    semesterData.value = semRes.data || []
  } catch (e) { console.error(e) }
}

onMounted(() => fetchData())
</script>

<style scoped>
.analysis-page { display: flex; flex-direction: column; gap: 16px; }
.welcome-banner { padding: 24px 28px; background: linear-gradient(135deg, #FF6900 0%, #FF8A3D 100%); border-radius: var(--mi-radius-md); color: #fff; }
.welcome-title { font-size: 20px; font-weight: 700; margin: 0 0 4px; }
.welcome-desc { font-size: 13px; opacity: 0.85; margin: 0; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.chart-card, .table-card { background: var(--mi-bg-card); border-radius: var(--mi-radius-md); padding: 20px; box-shadow: var(--mi-shadow-sm); }
.chart-title { font-size: 16px; font-weight: 600; margin: 0 0 16px; color: var(--mi-text-primary); }
.metric-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; padding: 20px 0; }
.metric-item { text-align: center; }
.metric-value { font-size: 32px; font-weight: 700; }
.metric-label { font-size: 13px; color: var(--mi-text-secondary); margin-top: 4px; }
.table-card { margin-top: 16px; }
@media (max-width: 768px) { .chart-row { grid-template-columns: 1fr; } }
</style>
