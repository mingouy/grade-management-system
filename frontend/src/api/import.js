import request from '@/utils/request'

export function importScores(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/import/scores',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function downloadTemplate() {
  return request({
    url: '/import/score-template',
    method: 'get',
    responseType: 'blob'
  })
}
