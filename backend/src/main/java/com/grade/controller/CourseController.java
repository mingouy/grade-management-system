package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.Course;
import com.grade.exception.BusinessException;
import com.grade.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 获取课程列表
     */
    @GetMapping
    public Result<List<Course>> list() {
        List<Course> list = courseService.list();
        return Result.success(list);
    }

    /**
     * 获取教师的授课列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Course>> listByTeacherId(@PathVariable Long teacherId) {
        List<Course> list = courseService.listByTeacherId(teacherId);
        return Result.success(list);
    }

    /**
     * 新增课程
     */
    @PostMapping
    public Result<Void> save(@RequestBody Course course) {
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new BusinessException("课程名称不能为空");
        }
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new BusinessException("课程编码不能为空");
        }
        if (course.getCredit() == null) {
            throw new BusinessException("学分不能为空");
        }
        if (course.getHours() == null) {
            throw new BusinessException("学时不能为空");
        }
        if (course.getTeacherId() == null) {
            throw new BusinessException("授课教师不能为空");
        }
        if (course.getSemester() == null || course.getSemester().trim().isEmpty()) {
            throw new BusinessException("开课学期不能为空");
        }

        courseService.save(course);
        return Result.success("新增成功", null);
    }

    /**
     * 修改课程
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Course course) {
        Course exist = courseService.getById(id);
        if (exist == null) {
            throw new BusinessException("课程不存在");
        }

        course.setId(id);
        courseService.updateById(course);
        return Result.success("修改成功", null);
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Course exist = courseService.getById(id);
        if (exist == null) {
            throw new BusinessException("课程不存在");
        }

        courseService.removeById(id);
        return Result.success("删除成功", null);
    }
}
