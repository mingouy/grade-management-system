package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.CourseSelection;
import com.grade.entity.Score;
import com.grade.entity.StudentInfo;
import com.grade.exception.BusinessException;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.ScoreService;
import com.grade.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生端接口控制器
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private ScoreService scoreService;

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

    @GetMapping("/score-trend")
    public Result<List<Map<String, Object>>> scoreTrend() {
        Long userId = AuthInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        StudentInfo student = studentInfoService.getByUserId(userId);
        if (student == null) return Result.success(new ArrayList<>());

        List<Score> scores = scoreService.listByStudentId(student.getId());

        Map<String, List<Score>> bySemester = scores.stream()
            .filter(s -> s.getSemester() != null)
            .collect(Collectors.groupingBy(Score::getSemester));

        List<Map<String, Object>> result = new ArrayList<>();
        bySemester.forEach((semester, semesterScores) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("semester", semester);

            double avg = semesterScores.stream().filter(s -> s.getScore() != null)
                .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);
            item.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());
            item.put("courseCount", semesterScores.size());

            long passCount = semesterScores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60).count();
            item.put("passRate", new BigDecimal((double) passCount / semesterScores.size() * 100).setScale(1, RoundingMode.HALF_UP).toString());

            result.add(item);
        });

        result.sort((a, b) -> ((String) a.get("semester")).compareTo((String) b.get("semester")));
        return Result.success(result);
    }
}
