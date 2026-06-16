package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.TeacherInfo;
import com.grade.exception.BusinessException;
import com.grade.service.TeacherInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher-infos")
public class TeacherInfoController {

    @Autowired
    private TeacherInfoService teacherInfoService;

    @GetMapping
    public Result<List<TeacherInfo>> list(@RequestParam(required = false) Long deptId) {
        List<TeacherInfo> list;
        if (deptId != null) {
            list = teacherInfoService.listByDeptId(deptId);
        } else {
            list = teacherInfoService.listWithDetails();
        }
        return Result.success(list);
    }

    @PostMapping
    public Result<Void> save(@RequestBody TeacherInfo teacher) {
        if (teacher.getTeacherNo() == null || teacher.getTeacherNo().trim().isEmpty()) {
            throw new BusinessException("教师工号不能为空");
        }
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            throw new BusinessException("教师姓名不能为空");
        }
        if (teacher.getDeptId() == null) {
            throw new BusinessException("所属专业不能为空");
        }
        teacherInfoService.save(teacher);
        return Result.success("新增成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody TeacherInfo teacher) {
        TeacherInfo exist = teacherInfoService.getById(id);
        if (exist == null) {
            throw new BusinessException("教师信息不存在");
        }
        teacher.setId(id);
        teacherInfoService.updateById(teacher);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        TeacherInfo exist = teacherInfoService.getById(id);
        if (exist == null) {
            throw new BusinessException("教师信息不存在");
        }
        teacherInfoService.removeById(id);
        return Result.success("删除成功", null);
    }
}
