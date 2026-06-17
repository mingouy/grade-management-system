package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.SysUser;
import com.grade.exception.BusinessException;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.SysUserService;
import cn.hutool.crypto.digest.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    public Result<SysUser> getProfile() {
        Long userId = AuthInterceptor.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping
    public Result<Void> updateProfile(@RequestBody Map<String, String> params) {
        Long userId = AuthInterceptor.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (params.containsKey("realName")) user.setRealName(params.get("realName"));
        if (params.containsKey("phone")) user.setPhone(params.get("phone"));
        if (params.containsKey("email")) user.setEmail(params.get("email"));
        sysUserService.updateById(user);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        Long userId = AuthInterceptor.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) throw new BusinessException("密码不能为空");
        String encryptOld = MD5.create().digestHex(oldPassword);
        if (!encryptOld.equals(user.getPassword())) throw new BusinessException("原密码错误");
        if (newPassword.length() < 6) throw new BusinessException("新密码长度不能少于6位");
        user.setPassword(MD5.create().digestHex(newPassword));
        sysUserService.updateById(user);
        return Result.success();
    }
}
