import request from '@/utils/request'

export function getGradeDistribution(params) {
  return request({ url: '/grade-analysis/distribution', method: 'get', params })
}

export function getGradeRanking(params) {
  return request({ url: '/grade-analysis/ranking', method: 'get', params })
}

export function getFailingStudents() {
  return request({ url: '/grade-analysis/failing', method: 'get' })
}

export function getSemesterComparison() {
  return request({ url: '/grade-analysis/semester-comparison', method: 'get' })
}
