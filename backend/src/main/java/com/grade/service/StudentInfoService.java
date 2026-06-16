package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.CourseSelection;
import com.grade.entity.Score;
import com.grade.entity.StudentInfo;

import java.util.List;

/**
 * 学生信息Service接口
 */
public interface StudentInfoService extends IService<StudentInfo> {

    /**
     * 根据用户ID查询学生信息
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    StudentInfo getByUserId(Long userId);

    /**
     * 根据班级ID查询学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    List<StudentInfo> listByClassId(Long classId);

    /**
     * 根据学生ID查询已选课程列表
     *
     * @param studentId 学生ID
     * @return 已选课程列表
     */
    List<CourseSelection> listCoursesByStudentId(Long studentId);

    /**
     * 根据学生ID查询成绩列表
     *
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<Score> listScoresByStudentId(Long studentId);

    /**
     * 查询所有学生信息（含详情）
     *
     * @return 学生列表
     */
    List<StudentInfo> listWithDetails();

    /**
     * 根据专业ID查询学生列表
     *
     * @param deptId 专业ID
     * @return 学生列表
     */
    List<StudentInfo> listByDeptId(Long deptId);
}
