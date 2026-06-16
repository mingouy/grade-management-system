package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.TeacherInfo;

import java.util.List;

/**
 * 教师信息Service接口
 */
public interface TeacherInfoService extends IService<TeacherInfo> {

    /**
     * 根据用户ID查询教师信息
     *
     * @param userId 用户ID
     * @return 教师信息
     */
    TeacherInfo getByUserId(Long userId);

    /**
     * 根据专业ID查询教师列表
     *
     * @param deptId 专业ID
     * @return 教师列表
     */
    List<TeacherInfo> listByDeptId(Long deptId);

    /**
     * 查询所有教师信息（含详情）
     *
     * @return 教师列表
     */
    List<TeacherInfo> listWithDetails();
}
