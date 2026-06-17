package com.grade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.entity.Notification;
import com.grade.mapper.NotificationMapper;
import com.grade.service.NotificationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public Map<String, Object> listByUserId(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId).orderByDesc(Notification::getCreateTime);
        Page<Notification> pageResult = page(new Page<>(page, size), wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("current", page);
        result.put("size", size);
        return result;
    }

    @Override
    public long countUnread(Long userId) {
        return count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0));
    }

    @Override
    public boolean markAsRead(Long id) {
        Notification notification = getById(id);
        if (notification != null) {
            notification.setIsRead(1);
            return updateById(notification);
        }
        return false;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        Notification update = new Notification();
        update.setIsRead(1);
        return update(update, new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId).eq(Notification::getIsRead, 0));
    }

    @Override
    public boolean createNotification(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        return save(notification);
    }
}
