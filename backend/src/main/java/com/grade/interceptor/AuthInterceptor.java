package com.grade.interceptor;

import com.grade.common.Constants;
import com.grade.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * ThreadLocal存储当前登录用户信息
     * 格式: userId_role
     */
    private static final ThreadLocal<String> USER_CONTEXT = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行认证相关接口
        String uri = request.getRequestURI();
        if (uri.contains("/auth/")) {
            return true;
        }

        // 从请求头获取token
        String token = request.getHeader(Constants.TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        // 移除Bearer前缀
        if (token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }

        // 简化token校验: token格式为 userId_role
        if (!token.contains("_")) {
            throw new BusinessException(401, "无效的Token");
        }

        // 存入ThreadLocal和request attribute
        USER_CONTEXT.set(token);
        request.setAttribute("userToken", token);

        // RBAC角色权限校验
        String role = getCurrentRole();
        if (role == null) {
            throw new BusinessException(403, "无权限访问");
        }

        // 管理员可访问所有接口
        if ("admin".equals(role)) {
            return true;
        }

        // 教师角色权限
        if ("teacher".equals(role)) {
            // 教师可访问: /teacher/*, /dashboard, /classes, /courses, /scores, /auth, /profile, /notifications, /export
            if (uri.contains("/teacher") || uri.contains("/dashboard") || uri.contains("/classes")
                    || uri.contains("/courses") || uri.contains("/scores") || uri.contains("/auth")
                    || uri.contains("/profile") || uri.contains("/notifications") || uri.contains("/export")
                    || uri.contains("/grade-analysis") || uri.contains("/import")) {
                return true;
            }
            throw new BusinessException(403, "无权限访问");
        }

        // 学生角色权限
        if ("student".equals(role)) {
            // 学生可访问: /student/*, /dashboard, /auth, /course-selections, /profile, /notifications, /export
            if (uri.contains("/student") || uri.contains("/dashboard") || uri.contains("/auth")
                    || uri.contains("/course-selections") || uri.contains("/profile") || uri.contains("/notifications")
                    || uri.contains("/export") || uri.contains("/grade-analysis")) {
                return true;
            }
            throw new BusinessException(403, "无权限访问");
        }

        throw new BusinessException(403, "无权限访问");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理ThreadLocal，防止内存泄漏
        USER_CONTEXT.remove();
    }

    /**
     * 获取当前登录用户Token
     */
    public static String getCurrentToken() {
        return USER_CONTEXT.get();
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        String token = USER_CONTEXT.get();
        if (token == null || !token.contains("_")) {
            return null;
        }
        try {
            return Long.parseLong(token.split("_")[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentRole() {
        String token = USER_CONTEXT.get();
        if (token == null || !token.contains("_")) {
            return null;
        }
        String[] parts = token.split("_");
        return parts.length > 1 ? parts[1] : null;
    }
}
