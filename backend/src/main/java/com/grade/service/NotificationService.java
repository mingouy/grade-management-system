package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.Notification;
import java.util.Map;

public interface NotificationService extends IService<Notification> {
    Map<String, Object> listByUserId(Long userId, Integer page, Integer size);
    long countUnread(Long userId);
    boolean markAsRead(Long id);
    boolean markAllAsRead(Long userId);
    boolean createNotification(Long userId, String title, String content, String type);
}
