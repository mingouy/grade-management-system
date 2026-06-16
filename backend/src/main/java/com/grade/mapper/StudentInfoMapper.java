package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生信息Mapper接口
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {

    /**
     * 根据用户ID查询学生信息（含专业和班级名称）
     */
    @Select("SELECT s.*, d.dept_name, c.class_name, u.username FROM student_info s " +
            "LEFT JOIN department d ON s.dept_id = d.id " +
            "LEFT JOIN class_info c ON s.class_id = c.id " +
            "LEFT JOIN sys_user u ON s.user_id = u.id " +
            "WHERE s.user_id = #{userId}")
    StudentInfo selectByUserId(Long userId);

    /**
     * 查询所有学生信息（含专业和班级名称）
     */
    @Select("SELECT s.*, d.dept_name, c.class_name, u.username FROM student_info s " +
            "LEFT JOIN department d ON s.dept_id = d.id " +
            "LEFT JOIN class_info c ON s.class_id = c.id " +
            "LEFT JOIN sys_user u ON s.user_id = u.id")
    List<StudentInfo> selectListWithDetails();

    /**
     * 根据班级ID查询学生列表
     */
    @Select("SELECT s.*, d.dept_name, c.class_name, u.username FROM student_info s " +
            "LEFT JOIN department d ON s.dept_id = d.id " +
            "LEFT JOIN class_info c ON s.class_id = c.id " +
            "LEFT JOIN sys_user u ON s.user_id = u.id " +
            "WHERE s.class_id = #{classId}")
    List<StudentInfo> selectListByClassId(Long classId);

    /**
     * 根据专业ID查询学生列表
     */
    @Select("SELECT s.*, d.dept_name, c.class_name, u.username FROM student_info s " +
            "LEFT JOIN department d ON s.dept_id = d.id " +
            "LEFT JOIN class_info c ON s.class_id = c.id " +
            "LEFT JOIN sys_user u ON s.user_id = u.id " +
            "WHERE s.dept_id = #{deptId}")
    List<StudentInfo> selectListByDeptId(Long deptId);
}
