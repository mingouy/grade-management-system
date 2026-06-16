package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.StudentInfo;
import com.grade.exception.BusinessException;
import com.grade.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-infos")
public class StudentInfoController {

    @Autowired
    private StudentInfoService studentInfoService;

    @GetMapping
    public Result<List<StudentInfo>> list(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long deptId) {
        List<StudentInfo> list;
        if (classId != null) {
            list = studentInfoService.listByClassId(classId);
        } else if (deptId != null) {
            list = studentInfoService.listByDeptId(deptId);
        } else {
            list = studentInfoService.listWithDetails();
        }
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> save(@RequestBody StudentInfo student) {
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            throw new BusinessException("学号不能为空");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new BusinessException("学生姓名不能为空");
        }
        if (student.getDeptId() == null) {
            throw new BusinessException("所属专业不能为空");
        }
        if (student.getClassId() == null) {
            throw new BusinessException("所属班级不能为空");
        }
        studentInfoService.save(student);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StudentInfo student) {
        StudentInfo exist = studentInfoService.getById(id);
        if (exist == null) {
            throw new BusinessException("学生信息不存在");
        }
        student.setId(id);
        studentInfoService.updateById(student);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        StudentInfo exist = studentInfoService.getById(id);
        if (exist == null) {
            throw new BusinessException("学生信息不存在");
        }
        studentInfoService.removeById(id);
        return Result.success("删除成功", null);
    }
}
