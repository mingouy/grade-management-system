package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.*;
import com.grade.exception.BusinessException;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired private TeacherInfoService teacherInfoService;
    @Autowired private CourseService courseService;
    @Autowired private ClassInfoService classInfoService;
    @Autowired private StudentInfoService studentInfoService;
    @Autowired private ScoreService scoreService;

    @GetMapping("/info")
    public Result<TeacherInfo> info() {
        Long userId = AuthInterceptor.getCurrentUserId();
        if (userId == null) throw new BusinessException(401, "未登录或登录已过期");
        TeacherInfo teacherInfo = teacherInfoService.getByUserId(userId);
        if (teacherInfo == null) throw new BusinessException("教师信息不存在");
        return Result.success(teacherInfo);
    }

    @GetMapping("/courses")
    public Result<List<Course>> courses() {
        Long userId = AuthInterceptor.getCurrentUserId();
        TeacherInfo teacherInfo = teacherInfoService.getByUserId(userId);
        if (teacherInfo == null) throw new BusinessException("教师信息不存在");
        return Result.success(courseService.listByTeacherId(teacherInfo.getId()));
    }

    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> classes() {
        Long userId = AuthInterceptor.getCurrentUserId();
        TeacherInfo teacherInfo = teacherInfoService.getByUserId(userId);
        if (teacherInfo == null) throw new BusinessException("教师信息不存在");

        List<Course> courses = courseService.listByTeacherId(teacherInfo.getId());
        Set<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toSet());

        List<Map<String, Object>> result = new ArrayList<>();
        List<ClassInfo> allClasses = classInfoService.listAll();
        for (ClassInfo cls : allClasses) {
            List<StudentInfo> students = studentInfoService.listByClassId(cls.getId());
            boolean hasStudentInCourse = false;
            for (StudentInfo s : students) {
                List<Score> scores = scoreService.listByStudentId(s.getId());
                boolean match = scores.stream().anyMatch(sc -> courseIds.contains(sc.getCourseId()));
                if (match) { hasStudentInCourse = true; break; }
            }
            if (hasStudentInCourse) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", cls.getId());
                map.put("className", cls.getClassName());
                map.put("classCode", cls.getClassCode());
                map.put("gradeYear", cls.getGradeYear());
                map.put("deptName", cls.getDeptName());
                map.put("studentCount", students.size());
                result.add(map);
            }
        }
        return Result.success(result);
    }

    @GetMapping("/classes/{classId}/students")
    public Result<List<Map<String, Object>>> classStudents(@PathVariable Long classId) {
        Long userId = AuthInterceptor.getCurrentUserId();
        TeacherInfo teacherInfo = teacherInfoService.getByUserId(userId);
        if (teacherInfo == null) throw new BusinessException("教师信息不存在");

        List<Course> courses = courseService.listByTeacherId(teacherInfo.getId());
        Set<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toSet());

        List<StudentInfo> students = studentInfoService.listByClassId(classId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentInfo s : students) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", s.getId());
            map.put("studentNo", s.getStudentNo());
            map.put("name", s.getName());
            map.put("gender", s.getGender());
            map.put("phone", s.getPhone());
            map.put("email", s.getEmail());
            result.add(map);
        }
        return Result.success(result);
    }
}
