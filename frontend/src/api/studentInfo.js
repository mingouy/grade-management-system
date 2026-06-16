import request from '@/utils/request'

export function getStudentInfoList(params) {
  return request({
    url: '/student-infos',
    method: 'get',
    params
  })
}

export function createStudentInfo(data) {
  return request({
    url: '/student-infos',
    method: 'post',
    data
  })
}

export function updateStudentInfo(id, data) {
  return request({
    url: `/student-infos/${id}`,
    method: 'put',
    data
  })
}

export function deleteStudentInfo(id) {
  return request({
    url: `/student-infos/${id}`,
    method: 'delete'
  })
}
