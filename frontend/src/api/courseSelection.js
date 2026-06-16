import request from '@/utils/request'

export function getMyCourses() {
  return request({ url: '/course-selections/my', method: 'get' })
}

export function getAvailableCourses(params) {
  return request({ url: '/course-selections/available', method: 'get', params })
}

export function selectCourse(data) {
  return request({ url: '/course-selections/select', method: 'post', data })
}

export function dropCourse(id) {
  return request({ url: `/course-selections/drop/${id}`, method: 'delete' })
}
