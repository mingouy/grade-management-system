package com.grade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.Course;
import com.grade.mapper.CourseMapper;
import com.grade.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程Service实现类
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public List<Course> listByTeacherId(Long teacherId) {
        return baseMapper.selectListByTeacherId(teacherId);
    }
}
