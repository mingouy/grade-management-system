package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.ClassInfo;
import com.grade.exception.BusinessException;
import com.grade.service.ClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@RestController
@RequestMapping("/classes")
public class ClassInfoController {

    @Autowired
    private ClassInfoService classInfoService;

    /**
     * 获取班级列表（支持按专业筛选）
     */
    @GetMapping
    public Result<List<ClassInfo>> list(@RequestParam(required = false) Long deptId) {
        List<ClassInfo> list;
        if (deptId != null) {
            list = classInfoService.listByDeptId(deptId);
        } else {
            list = classInfoService.listAll();
        }
        return Result.success(list);
    }

    /**
     * 获取所有班级（下拉用）
     */
    @GetMapping("/all")
    public Result<List<ClassInfo>> listAll() {
        List<ClassInfo> list = classInfoService.listAll();
        return Result.success(list);
    }

    /**
     * 新增班级
     */
    @PostMapping
    public Result<Void> save(@RequestBody ClassInfo classInfo) {
        if (classInfo.getClassName() == null || classInfo.getClassName().trim().isEmpty()) {
            throw new BusinessException("班级名称不能为空");
        }
        if (classInfo.getClassCode() == null || classInfo.getClassCode().trim().isEmpty()) {
            throw new BusinessException("班级编码不能为空");
        }
        if (classInfo.getDeptId() == null) {
            throw new BusinessException("所属专业不能为空");
        }
        if (classInfo.getGradeYear() == null) {
            throw new BusinessException("年级不能为空");
        }

        classInfoService.save(classInfo);
        return Result.success("新增成功", null);
    }

    /**
     * 修改班级
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        ClassInfo exist = classInfoService.getById(id);
        if (exist == null) {
            throw new BusinessException("班级不存在");
        }

        classInfo.setId(id);
        classInfoService.updateById(classInfo);
        return Result.success("修改成功", null);
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ClassInfo exist = classInfoService.getById(id);
        if (exist == null) {
            throw new BusinessException("班级不存在");
        }

        classInfoService.removeById(id);
        return Result.success("删除成功", null);
    }
}
