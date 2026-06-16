import request from '@/utils/request'

export function getDepartmentList(params) {
  return request({
    url: '/departments',
    method: 'get',
    params
  })
}

export function createDepartment(data) {
  return request({
    url: '/departments',
    method: 'post',
    data
  })
}

export function updateDepartment(id, data) {
  return request({
    url: `/departments/${id}`,
    method: 'put',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: `/departments/${id}`,
    method: 'delete'
  })
}
