import request from '@/utils/request'

export function exportScores(params) {
  return request({ url: '/export/scores', method: 'get', params, responseType: 'blob' })
}

export function exportMyScores() {
  return request({ url: '/export/my-scores', method: 'get', responseType: 'blob' })
}
