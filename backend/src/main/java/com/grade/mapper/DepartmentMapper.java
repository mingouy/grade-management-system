package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专业Mapper接口
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
