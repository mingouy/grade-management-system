package com.grade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grade.common.Constants;
import com.grade.common.PageResult;
import com.grade.common.Result;
import com.grade.entity.SysUser;
import com.grade.exception.BusinessException;
import com.grade.service.SysUserService;
import cn.hutool.crypto.digest.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器（管理员）
 */
@RestController
@RequestMapping("/users")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表（支持按角色、用户名筛选）
     */
    @GetMapping
    public Result<PageResult<SysUser>> list(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 角色筛选
        if (role != null && !role.trim().isEmpty()) {
            wrapper.eq(SysUser::getRole, role);
        }

        // 用户名模糊查询
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(SysUser::getUsername, username.trim());
        }

        Page<SysUser> pageResult = sysUserService.page(new Page<>(page, size), wrapper);

        // 脱敏处理
        List<SysUser> records = pageResult.getRecords();
        records.forEach(user -> user.setPassword(null));

        PageResult<SysUser> result = new PageResult<>(
                pageResult.getTotal(),
                records,
                pageResult.getCurrent(),
                pageResult.getSize()
        );

        return Result.success(result);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> save(@RequestBody SysUser user) {
        // 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            throw new BusinessException("真实姓名不能为空");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new BusinessException("角色不能为空");
        }

        // 检查用户名是否已存在
        SysUser exist = sysUserService.getByUsername(user.getUsername().trim());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }

        // 密码MD5加密
        user.setPassword(MD5.create().digestHex(user.getPassword().trim()));
        user.setUsername(user.getUsername().trim());
        user.setStatus(Constants.STATUS_NORMAL);

        sysUserService.save(user);
        return Result.success("新增成功", null);
    }

    /**
     * 修改用户
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser exist = sysUserService.getById(id);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }

        user.setId(id);
        // 不允许修改密码（需单独接口）
        user.setPassword(null);

        sysUserService.updateById(user);
        return Result.success("修改成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        SysUser exist = sysUserService.getById(id);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }

        sysUserService.removeById(id);
        return Result.success("删除成功", null);
    }
}
