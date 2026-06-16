package com.grade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.CourseSelection;
import com.grade.entity.Score;
import com.grade.entity.StudentInfo;
import com.grade.mapper.CourseSelectionMapper;
import com.grade.mapper.ScoreMapper;
import com.grade.mapper.StudentInfoMapper;
import com.grade.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生信息Service实现类
 */
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public StudentInfo getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<StudentInfo> listByClassId(Long classId) {
        return baseMapper.selectListByClassId(classId);
    }

    @Override
    public List<CourseSelection> listCoursesByStudentId(Long studentId) {
        return courseSelectionMapper.selectListByStudentId(studentId);
    }

    @Override
    public List<Score> listScoresByStudentId(Long studentId) {
        return scoreMapper.selectListByStudentId(studentId);
    }

    @Override
    public List<StudentInfo> listWithDetails() {
        return baseMapper.selectListWithDetails();
    }

    @Override
    public List<StudentInfo> listByDeptId(Long deptId) {
        return baseMapper.selectListByDeptId(deptId);
    }
}
