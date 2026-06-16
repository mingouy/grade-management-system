import request from '@/utils/request'

export function getTeacherInfoList(params) {
  return request({
    url: '/teacher-infos',
    method: 'get',
    params
  })
}

export function createTeacherInfo(data) {
  return request({
    url: '/teacher-infos',
    method: 'post',
    data
  })
}

export function updateTeacherInfo(id, data) {
  return request({
    url: `/teacher-infos/${id}`,
    method: 'put',
    data
  })
}

export function deleteTeacherInfo(id) {
  return request({
    url: `/teacher-infos/${id}`,
    method: 'delete'
  })
}
