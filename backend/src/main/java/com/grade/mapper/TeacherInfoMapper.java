package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.TeacherInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教师信息Mapper接口
 */
@Mapper
public interface TeacherInfoMapper extends BaseMapper<TeacherInfo> {

    /**
     * 根据用户ID查询教师信息（含专业名称）
     */
    @Select("SELECT t.*, d.dept_name, u.username FROM teacher_info t " +
            "LEFT JOIN department d ON t.dept_id = d.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "WHERE t.user_id = #{userId}")
    TeacherInfo selectByUserId(Long userId);

    /**
     * 查询所有教师信息（含专业名称）
     */
    @Select("SELECT t.*, d.dept_name, u.username FROM teacher_info t " +
            "LEFT JOIN department d ON t.dept_id = d.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id")
    List<TeacherInfo> selectListWithDetails();

    /**
     * 根据专业ID查询教师列表
     */
    @Select("SELECT t.*, d.dept_name, u.username FROM teacher_info t " +
            "LEFT JOIN department d ON t.dept_id = d.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "WHERE t.dept_id = #{deptId}")
    List<TeacherInfo> selectListByDeptId(Long deptId);
}
