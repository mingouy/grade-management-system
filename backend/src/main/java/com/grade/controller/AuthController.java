package com.grade.controller;

import com.grade.common.Result;
import com.grade.entity.SysUser;
import com.grade.exception.BusinessException;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户登录
     *
     * @param params 请求参数（username, password）
     * @return token和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        Map<String, Object> result = sysUserService.login(username.trim(), password.trim());
        return Result.success("登录成功", result);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> info(HttpServletRequest request) {
        Long userId = AuthInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 脱敏处理
        user.setPassword(null);
        return Result.success(user);
    }
}
