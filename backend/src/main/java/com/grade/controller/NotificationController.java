package com.grade.controller;

import com.grade.common.Result;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = AuthInterceptor.getCurrentUserId();
        return Result.success(notificationService.listByUserId(userId, page, size));
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount() {
        Long userId = AuthInterceptor.getCurrentUserId();
        Map<String, Object> data = new HashMap<>();
        data.put("count", notificationService.countUnread(userId));
        return Result.success(data);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = AuthInterceptor.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}
