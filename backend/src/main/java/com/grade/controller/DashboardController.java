package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.*;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired private StudentInfoService studentInfoService;
    @Autowired private TeacherInfoService teacherInfoService;
    @Autowired private CourseService courseService;
    @Autowired private DepartmentService departmentService;
    @Autowired private ScoreService scoreService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        String role = AuthInterceptor.getCurrentRole();
        Long userId = AuthInterceptor.getCurrentUserId();
        Map<String, Object> stats = new HashMap<>();

        if ("admin".equals(role)) {
            stats.put("studentCount", studentInfoService.count());
            stats.put("teacherCount", teacherInfoService.count());
            stats.put("courseCount", courseService.count());
            stats.put("departmentCount", departmentService.count());

            List<Score> allScores = scoreService.list();
            if (!allScores.isEmpty()) {
                long passCount = allScores.stream()
                    .filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60).count();
                double passRate = (double) passCount / allScores.size() * 100;
                stats.put("passRate", new BigDecimal(passRate).setScale(1, RoundingMode.HALF_UP).toString());
                double avg = allScores.stream().filter(s -> s.getScore() != null)
                    .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);
                stats.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());
            } else {
                stats.put("passRate", "0.0");
                stats.put("avgScore", "0.0");
            }
        } else if ("teacher".equals(role)) {
            TeacherInfo teacher = teacherInfoService.getByUserId(userId);
            if (teacher != null) {
                List<Course> courses = courseService.listByTeacherId(teacher.getId());
                stats.put("courseCount", courses.size());
                List<Score> scores = new ArrayList<>();
                for (Course c : courses) {
                    scores.addAll(scoreService.listByCourseId(c.getId()));
                }
                stats.put("studentCount", scores.size());
                if (!scores.isEmpty()) {
                    double avg = scores.stream().filter(s -> s.getScore() != null)
                        .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);
                    stats.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());
                } else {
                    stats.put("avgScore", "0.0");
                }
            }
        } else if ("student".equals(role)) {
            StudentInfo student = studentInfoService.getByUserId(userId);
            if (student != null) {
                List<CourseSelection> selections = studentInfoService.listCoursesByStudentId(student.getId());
                List<Score> scores = studentInfoService.listScoresByStudentId(student.getId());
                stats.put("courseCount", selections.size());
                if (!scores.isEmpty()) {
                    double avg = scores.stream().filter(s -> s.getScore() != null)
                        .mapToDouble(s -> s.getScore().doubleValue()).average().orElse(0);
                    stats.put("avgScore", new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP).toString());
                    long passCount = scores.stream().filter(s -> s.getScore() != null && s.getScore().doubleValue() >= 60).count();
                    double passRate = (double) passCount / scores.size() * 100;
                    stats.put("passRate", new BigDecimal(passRate).setScale(1, RoundingMode.HALF_UP).toString());
                } else {
                    stats.put("avgScore", "0.0");
                    stats.put("passRate", "0.0");
                }
            }
        }

        return Result.success(stats);
    }
}
