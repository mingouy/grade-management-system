package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 班级Mapper接口
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    /**
     * 根据专业ID查询班级列表
     */
    @Select("SELECT c.*, d.dept_name FROM class_info c LEFT JOIN department d ON c.dept_id = d.id WHERE c.dept_id = #{deptId}")
    List<ClassInfo> selectListByDeptId(Long deptId);

    /**
     * 查询所有班级（含专业名称）
     */
    @Select("SELECT c.*, d.dept_name FROM class_info c LEFT JOIN department d ON c.dept_id = d.id")
    List<ClassInfo> selectListWithDeptName();
}
