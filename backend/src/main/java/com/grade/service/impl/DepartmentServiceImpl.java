package com.grade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.Department;
import com.grade.mapper.DepartmentMapper;
import com.grade.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专业Service实现类
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<Department> listAll() {
        return list();
    }
}
