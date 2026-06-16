package com.grade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.ClassInfo;
import com.grade.mapper.ClassInfoMapper;
import com.grade.service.ClassInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级Service实现类
 */
@Service
public class ClassInfoServiceImpl extends ServiceImpl<ClassInfoMapper, ClassInfo> implements ClassInfoService {

    @Override
    public List<ClassInfo> listByDeptId(Long deptId) {
        return baseMapper.selectListByDeptId(deptId);
    }

    @Override
    public List<ClassInfo> listAll() {
        return baseMapper.selectListWithDeptName();
    }
}
