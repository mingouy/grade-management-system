package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.Course;
import com.grade.entity.CourseSelection;
import com.grade.exception.BusinessException;
import com.grade.interceptor.AuthInterceptor;
import com.grade.mapper.CourseSelectionMapper;
import com.grade.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/course-selections")
public class CourseSelectionController {

    @Autowired private CourseSelectionMapper courseSelectionMapper;
    @Autowired private StudentInfoService studentInfoService;
    @Autowired private CourseService courseService;

    @GetMapping("/my")
    public Result<List<CourseSelection>> myCourses() {
        Long userId = AuthInterceptor.getCurrentUserId();
        var student = studentInfoService.getByUserId(userId);
        if (student == null) throw new BusinessException("学生信息不存在");
        return Result.success(courseSelectionMapper.selectListByStudentId(student.getId()));
    }

    @GetMapping("/available")
    public Result<List<Course>> availableCourses(@RequestParam(required = false) String semester) {
        List<Course> list;
        if (semester != null && !semester.trim().isEmpty()) {
            list = courseService.list().stream()
                .filter(c -> semester.equals(c.getSemester()))
                .toList();
        } else {
            list = courseService.list();
        }
        return Result.success(list);
    }

    @PostMapping("/select")
    @Transactional
    public Result<Void> selectCourse(@RequestBody CourseSelection selection) {
        Long userId = AuthInterceptor.getCurrentUserId();
        var student = studentInfoService.getByUserId(userId);
        if (student == null) throw new BusinessException("学生信息不存在");
        if (selection.getCourseId() == null) throw new BusinessException("请选择课程");

        var course = courseService.getById(selection.getCourseId());
        if (course == null) throw new BusinessException("课程不存在");

        var existing = courseSelectionMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseSelection>()
                .eq(CourseSelection::getStudentId, student.getId())
                .eq(CourseSelection::getCourseId, selection.getCourseId())
                .eq(CourseSelection::getSemester, course.getSemester()));
        if (existing != null) throw new BusinessException("已选过该课程");

        CourseSelection cs = new CourseSelection();
        cs.setStudentId(student.getId());
        cs.setCourseId(selection.getCourseId());
        cs.setSemester(course.getSemester());
        cs.setSelectTime(LocalDateTime.now());
        courseSelectionMapper.insert(cs);
        return Result.success("选课成功", null);
    }

    @DeleteMapping("/drop/{id}")
    @Transactional
    public Result<Void> dropCourse(@PathVariable Long id) {
        Long userId = AuthInterceptor.getCurrentUserId();
        var student = studentInfoService.getByUserId(userId);
        if (student == null) throw new BusinessException("学生信息不存在");

        var selection = courseSelectionMapper.selectById(id);
        if (selection == null) throw new BusinessException("选课记录不存在");
        if (!selection.getStudentId().equals(student.getId())) throw new BusinessException("无权操作");

        courseSelectionMapper.deleteById(id);
        return Result.success("退课成功", null);
    }
}
