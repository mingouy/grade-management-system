package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 选课Mapper接口
 */
@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {

    /**
     * 根据学生ID查询选课列表（含课程信息）
     */
    @Select("SELECT cs.*, s.name as student_name, s.student_no, c.course_name, c.course_code, c.credit " +
            "FROM course_selection cs " +
            "LEFT JOIN student_info s ON cs.student_id = s.id " +
            "LEFT JOIN course c ON cs.course_id = c.id " +
            "WHERE cs.student_id = #{studentId} " +
            "ORDER BY cs.select_time DESC")
    List<CourseSelection> selectListByStudentId(Long studentId);

    /**
     * 根据课程ID查询选课列表
     */
    @Select("SELECT cs.*, s.name as student_name, s.student_no, c.course_name, c.course_code, c.credit " +
            "FROM course_selection cs " +
            "LEFT JOIN student_info s ON cs.student_id = s.id " +
            "LEFT JOIN course c ON cs.course_id = c.id " +
            "WHERE cs.course_id = #{courseId} " +
            "ORDER BY cs.select_time DESC")
    List<CourseSelection> selectListByCourseId(Long courseId);
}
