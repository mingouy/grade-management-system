import request from '@/utils/request'

export function getScoreList(params) {
  return request({
    url: '/scores',
    method: 'get',
    params
  })
}

export function createScore(data) {
  return request({
    url: '/scores',
    method: 'post',
    data
  })
}

export function updateScore(id, data) {
  return request({
    url: `/scores/${id}`,
    method: 'put',
    data
  })
}

export function deleteScore(id) {
  return request({
    url: `/scores/${id}`,
    method: 'delete'
  })
}

export function getScoresByCourse(courseId) {
  return request({
    url: `/scores/course/${courseId}`,
    method: 'get'
  })
}

export function batchCreateScores(data) {
  return request({
    url: '/scores/batch',
    method: 'post',
    data
  })
}
