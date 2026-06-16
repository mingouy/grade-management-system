package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.Department;
import com.grade.exception.BusinessException;
import com.grade.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业管理控制器
 */
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取专业列表
     */
    @GetMapping
    public Result<List<Department>> list() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

    /**
     * 新增专业
     */
    @PostMapping
    public Result<Void> save(@RequestBody Department department) {
        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            throw new BusinessException("专业名称不能为空");
        }
        if (department.getDeptCode() == null || department.getDeptCode().trim().isEmpty()) {
            throw new BusinessException("专业编码不能为空");
        }

        departmentService.save(department);
        return Result.success("新增成功", null);
    }

    /**
     * 修改专业
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Department department) {
        Department exist = departmentService.getById(id);
        if (exist == null) {
            throw new BusinessException("专业不存在");
        }

        department.setId(id);
        departmentService.updateById(department);
        return Result.success("修改成功", null);
    }

    /**
     * 删除专业
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Department exist = departmentService.getById(id);
        if (exist == null) {
            throw new BusinessException("专业不存在");
        }

        departmentService.removeById(id);
        return Result.success("删除成功", null);
    }
}
