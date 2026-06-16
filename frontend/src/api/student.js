import request from '@/utils/request'

export function getStudentInfo() {
  return request({
    url: '/student/info',
    method: 'get'
  })
}

export function getStudentCourses() {
  return request({
    url: '/student/courses',
    method: 'get'
  })
}

export function getStudentScores() {
  return request({
    url: '/student/scores',
    method: 'get'
  })
}
