package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.ClassInfo;

import java.util.List;

/**
 * 班级Service接口
 */
public interface ClassInfoService extends IService<ClassInfo> {

    /**
     * 根据专业ID查询班级列表
     *
     * @param deptId 专业ID
     * @return 班级列表
     */
    List<ClassInfo> listByDeptId(Long deptId);

    /**
     * 获取所有班级列表（下拉用）
     *
     * @return 班级列表
     */
    List<ClassInfo> listAll();
}
