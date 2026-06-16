package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.Department;

import java.util.List;

/**
 * 专业Service接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取所有专业列表（下拉用）
     *
     * @return 专业列表
     */
    List<Department> listAll();
}
