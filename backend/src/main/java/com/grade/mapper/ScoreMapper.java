package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    /**
     * 根据课程ID查询成绩列表（含学生和课程信息）
     */
    @Select("SELECT sc.*, s.name as student_name, s.student_no, c.course_name, c.course_code, c.credit " +
            "FROM score sc " +
            "LEFT JOIN student_info s ON sc.student_id = s.id " +
            "LEFT JOIN course c ON sc.course_id = c.id " +
            "WHERE sc.course_id = #{courseId} " +
            "ORDER BY sc.create_time DESC")
    List<Score> selectListByCourseId(Long courseId);

    /**
     * 根据学生ID查询成绩列表（含课程信息）
     */
    @Select("SELECT sc.*, s.name as student_name, s.student_no, c.course_name, c.course_code, c.credit " +
            "FROM score sc " +
            "LEFT JOIN student_info s ON sc.student_id = s.id " +
            "LEFT JOIN course c ON sc.course_id = c.id " +
            "WHERE sc.student_id = #{studentId} " +
            "ORDER BY sc.semester DESC, sc.create_time DESC")
    List<Score> selectListByStudentId(Long studentId);

    /**
     * 根据课程ID和学期查询成绩列表
     */
    @Select("SELECT sc.*, s.name as student_name, s.student_no, c.course_name, c.course_code, c.credit " +
            "FROM score sc " +
            "LEFT JOIN student_info s ON sc.student_id = s.id " +
            "LEFT JOIN course c ON sc.course_id = c.id " +
            "WHERE sc.course_id = #{courseId} AND sc.semester = #{semester} " +
            "ORDER BY sc.create_time DESC")
    List<Score> selectListByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

    /**
     * 查询所有成绩（含学生和课程信息）
     */
    @Select("SELECT sc.*, s.name as student_name, s.student_no, c.course_name, c.course_code, c.credit " +
            "FROM score sc " +
            "LEFT JOIN student_info s ON sc.student_id = s.id " +
            "LEFT JOIN course c ON sc.course_id = c.id " +
            "ORDER BY sc.create_time DESC")
    List<Score> selectListWithDetails();
}
