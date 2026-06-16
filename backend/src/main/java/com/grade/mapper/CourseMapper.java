package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 根据教师ID查询课程列表（含教师信息）
     */
    @Select("SELECT c.*, t.name as teacher_name, t.teacher_no FROM course c LEFT JOIN teacher_info t ON c.teacher_id = t.id WHERE c.teacher_id = #{teacherId}")
    List<Course> selectListByTeacherId(Long teacherId);

    /**
     * 查询所有课程（含教师信息）
     */
    @Select("SELECT c.*, t.name as teacher_name, t.teacher_no FROM course c LEFT JOIN teacher_info t ON c.teacher_id = t.id")
    List<Course> selectListWithTeacher();
}
