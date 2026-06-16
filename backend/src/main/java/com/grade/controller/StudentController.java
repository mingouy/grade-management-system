package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.CourseSelection;
import com.grade.entity.Score;
import com.grade.entity.StudentInfo;
import com.grade.exception.BusinessException;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学生端接口控制器
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentInfoService studentInfoService;

    /**
     * 获取学生个人信息
     */
    @GetMapping("/info")
    public Result<StudentInfo> info() {
        Long userId = AuthInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        StudentInfo studentInfo = studentInfoService.getByUserId(userId);
        if (studentInfo == null) {
            throw new BusinessException("学生信息不存在");
        }

        return Result.success(studentInfo);
    }

    /**
     * 获取已选课程
     */
    @GetMapping("/courses")
    public Result<List<CourseSelection>> courses() {
        Long userId = AuthInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        StudentInfo studentInfo = studentInfoService.getByUserId(userId);
        if (studentInfo == null) {
            throw new BusinessException("学生信息不存在");
        }

        List<CourseSelection> list = studentInfoService.listCoursesByStudentId(studentInfo.getId());
        return Result.success(list);
    }

    /**
     * 获取个人成绩
     */
    @GetMapping("/scores")
    public Result<List<Score>> scores() {
        Long userId = AuthInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        StudentInfo studentInfo = studentInfoService.getByUserId(userId);
        if (studentInfo == null) {
            throw new BusinessException("学生信息不存在");
        }

        List<Score> list = studentInfoService.listScoresByStudentId(studentInfo.getId());
        return Result.success(list);
    }
}
