package com.grade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.TeacherInfo;
import com.grade.mapper.TeacherInfoMapper;
import com.grade.service.TeacherInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教师信息Service实现类
 */
@Service
public class TeacherInfoServiceImpl extends ServiceImpl<TeacherInfoMapper, TeacherInfo> implements TeacherInfoService {

    @Override
    public TeacherInfo getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<TeacherInfo> listByDeptId(Long deptId) {
        return baseMapper.selectListByDeptId(deptId);
    }

    @Override
    public List<TeacherInfo> listWithDetails() {
        return baseMapper.selectListWithDetails();
    }
}
