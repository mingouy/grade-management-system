package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.Course;

import java.util.List;

/**
 * 课程Service接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 根据教师ID查询授课列表
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> listByTeacherId(Long teacherId);
}
