# Phase 1 - 个人信息管理 + 成绩导出 + 通知系统 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use compose:subagent (recommended) or compose:execute to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add personal profile management, Excel grade export, and in-app notification system to the grade management system.

**Architecture:** Backend adds 3 new controllers (ProfileController, ExportController, NotificationController) with corresponding services. Frontend adds profile page, export functionality, and notification bell component. Uses EasyExcel for export, MyBatis-Plus for notification CRUD.

**Tech Stack:** Spring Boot 4, MyBatis-Plus 3.5.16, EasyExcel 4.0.3, Vue 3, Element Plus, Axios

---

### Task 1: Add EasyExcel Dependency

**Covers:** Export functionality foundation

**Files:**
- Modify: `backend/pom.xml`

- [ ] **Step 1: Add EasyExcel dependency**

```xml
<!-- In pom.xml, after hutool-all dependency -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>4.0.3</version>
</dependency>
```

- [ ] **Step 2: Verify build succeeds**

Run: `cd backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

---

### Task 2: Create Notification Entity and Mapper

**Covers:** Notification system data layer

**Files:**
- Create: `backend/src/main/java/com/grade/entity/Notification.java`
- Create: `backend/src/main/java/com/grade/mapper/NotificationMapper.java`
- Modify: `sql/schema.sql` (add notification table)

- [ ] **Step 1: Create Notification entity**

```java
package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type; // system, grade, course
    private Integer isRead; // 0=unread, 1=read
    private LocalDateTime createTime;
}
```

- [ ] **Step 2: Create NotificationMapper**

```java
package com.grade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grade.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
```

- [ ] **Step 3: Add notification table to schema.sql**

```sql
-- Add to schema.sql
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT '通知类型: system/grade/course',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0=未读, 1=已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
```

- [ ] **Step 4: Execute SQL**

Run: `mysql -u root -p'root123456' grade_management < sql/schema.sql`

---

### Task 3: Create NotificationService

**Covers:** Notification business logic

**Files:**
- Create: `backend/src/main/java/com/grade/service/NotificationService.java`
- Create: `backend/src/main/java/com/grade/service/impl/NotificationServiceImpl.java`

- [ ] **Step 1: Create NotificationService interface**

```java
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
```

- [ ] **Step 2: Create NotificationServiceImpl**

```java
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
        wrapper.eq(Notification::getUserId, userId)
               .orderByDesc(Notification::getCreateTime);
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
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
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
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
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
```

---

### Task 4: Create NotificationController

**Covers:** Notification API endpoints

**Files:**
- Create: `backend/src/main/java/com/grade/controller/NotificationController.java`

- [ ] **Step 1: Create NotificationController**

```java
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
```

---

### Task 5: Create ProfileController

**Covers:** Personal info management

**Files:**
- Create: `backend/src/main/java/com/grade/controller/ProfileController.java`

- [ ] **Step 1: Create ProfileController**

```java
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
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping
    public Result<Void> updateProfile(@RequestBody Map<String, String> params) {
        Long userId = AuthInterceptor.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
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
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new BusinessException("密码不能为空");
        }
        String encryptOld = MD5.create().digestHex(oldPassword);
        if (!encryptOld.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }
        user.setPassword(MD5.create().digestHex(newPassword));
        sysUserService.updateById(user);
        return Result.success();
    }
}
```

---

### Task 6: Create ExportController

**Covers:** Excel export functionality

**Files:**
- Create: `backend/src/main/java/com/grade/controller/ExportController.java`

- [ ] **Step 1: Create ExportController**

```java
package com.grade.controller;

import com.alibaba.excel.EasyExcel;
import com.grade.entity.Score;
import com.grade.interceptor.AuthInterceptor;
import com.grade.service.ScoreService;
import com.grade.service.StudentInfoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentInfoService studentInfoService;

    @GetMapping("/scores")
    public void exportScores(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester,
            HttpServletResponse response) throws IOException {
        
        List<Score> scores;
        if (courseId != null) {
            if (semester != null && !semester.isEmpty()) {
                scores = scoreService.listByCourseIdAndSemester(courseId, semester);
            } else {
                scores = scoreService.listByCourseId(courseId);
            }
        } else {
            scores = scoreService.list();
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("成绩报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Score.class)
                .sheet("成绩报表")
                .doWrite(scores);
    }

    @GetMapping("/my-scores")
    public void exportMyScores(HttpServletResponse response) throws IOException {
        Long userId = AuthInterceptor.getCurrentUserId();
        var student = studentInfoService.getByUserId(userId);
        if (student == null) {
            return;
        }
        List<Score> scores = scoreService.listByStudentId(student.getId());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("我的成绩", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Score.class)
                .sheet("我的成绩")
                .doWrite(scores);
    }
}
```

---

### Task 7: Add Score Excel Annotations

**Covers:** Excel export data mapping

**Files:**
- Modify: `backend/src/main/java/com/grade/entity/Score.java`

- [ ] **Step 1: Add EasyExcel annotations to Score entity**

```java
// Add import at top
import com.alibaba.excel.annotation.ExcelProperty;

// Add annotations to fields
@TableId(type = IdType.AUTO)
@ExcelProperty("ID")
private Long id;

@ExcelProperty("学生ID")
private Long studentId;

@ExcelProperty("课程ID")
private Long courseId;

@ExcelProperty("成绩")
private BigDecimal score;

@ExcelProperty("学期")
private String semester;

@ExcelProperty("考试类型")
private String examType;

@ExcelProperty("学生姓名")
private String studentName;

@ExcelProperty("学号")
private String studentNo;

@ExcelProperty("课程名称")
private String courseName;

@ExcelProperty("课程编码")
private String courseCode;

@ExcelProperty("学分")
private BigDecimal credit;
```

---

### Task 8: Add Auth Interceptor Paths

**Covers:** API access control for new endpoints

**Files:**
- Modify: `backend/src/main/java/com/grade/interceptor/AuthInterceptor.java`

- [ ] **Step 1: Add new paths to interceptor bypass**

```java
// In preHandle method, after the /auth/ check, add:
if (uri.contains("/export/")) {
    // Export needs auth, so don't bypass
}
// The existing logic already handles role-based access
// Just make sure /notifications, /profile, /export are accessible
```

Actually, the existing interceptor already handles these correctly:
- `/notifications` - needs auth, accessible by all roles
- `/profile` - needs auth, accessible by all roles
- `/export` - needs auth, accessible by all roles

No changes needed to the interceptor.

---

### Task 9: Create Frontend Profile Page

**Covers:** Personal info management UI

**Files:**
- Create: `frontend/src/views/profile/ProfilePage.vue`
- Create: `frontend/src/api/profile.js`

- [ ] **Step 1: Create profile API module**

```javascript
import request from '@/utils/request'

export function getProfile() {
  return request({
    url: '/profile',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/profile',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/profile/password',
    method: 'put',
    data
  })
}
```

- [ ] **Step 2: Create ProfilePage.vue**

```vue
<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-wrap">
          <div class="avatar">{{ userInfo.realName ? userInfo.realName.charAt(0) : '?' }}</div>
        </div>
        <div class="user-meta">
          <h2 class="user-name">{{ userInfo.realName || '未知用户' }}</h2>
          <div class="tags">
            <el-tag type="primary" size="small">{{ roleLabel }}</el-tag>
          </div>
        </div>
      </div>

      <div class="profile-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="个人信息" name="info">
            <el-form :model="profileForm" label-width="80px" class="profile-form">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="姓名">
                <el-input v-model="profileForm.realName" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="修改密码" name="password">
            <el-form :model="passwordForm" label-width="100px" class="profile-form">
              <el-form-item label="原密码">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认新密码">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getProfile, updateProfile, changePassword } from '@/api/profile'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('info')
const userInfo = ref({})

const roleLabel = computed(() => {
  return { admin: '管理员', teacher: '教师', student: '学生' }[userStore.role] || ''
})

const profileForm = reactive({
  realName: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const fetchProfile = async () => {
  try {
    const res = await getProfile()
    userInfo.value = res.data || {}
    profileForm.realName = userInfo.value.realName || ''
    profileForm.phone = userInfo.value.phone || ''
    profileForm.email = userInfo.value.email || ''
  } catch (e) {
    console.error(e)
  }
}

const handleUpdateProfile = async () => {
  try {
    await updateProfile(profileForm)
    ElMessage.success('保存成功')
    userStore.setUserInfo({ ...userStore.userInfo, ...profileForm })
  } catch (e) {
    console.error(e)
  }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次密码不一致')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('密码长度不能少于6位')
    return
  }
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => fetchProfile())
</script>

<style scoped>
.profile-page {
  max-width: 800px;
}

.profile-card {
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-md);
  box-shadow: var(--mi-shadow-sm);
  overflow: hidden;
}

.profile-header {
  background: linear-gradient(135deg, var(--mi-primary), var(--mi-primary-light));
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: #fff;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.user-name {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
}

.profile-content {
  padding: 24px 32px;
}

.profile-form {
  max-width: 400px;
  margin-top: 16px;
}
</style>
```

---

### Task 10: Create Frontend Notification Components

**Covers:** Notification UI

**Files:**
- Create: `frontend/src/api/notification.js`
- Create: `frontend/src/components/NotificationBell.vue`
- Create: `frontend/src/views/notification/NotificationList.vue`

- [ ] **Step 1: Create notification API module**

```javascript
import request from '@/utils/request'

export function getNotifications(params) {
  return request({
    url: '/notifications',
    method: 'get',
    params
  })
}

export function getUnreadCount() {
  return request({
    url: '/notifications/unread-count',
    method: 'get'
  })
}

export function markAsRead(id) {
  return request({
    url: `/notifications/${id}/read`,
    method: 'put'
  })
}

export function markAllAsRead() {
  return request({
    url: '/notifications/read-all',
    method: 'put'
  })
}
```

- [ ] **Step 2: Create NotificationBell.vue component**

```vue
<template>
  <div class="notification-bell" @click="showDropdown = !showDropdown">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
      <el-icon :size="20"><Bell /></el-icon>
    </el-badge>
    
    <div v-if="showDropdown" class="notification-dropdown">
      <div class="dropdown-header">
        <span>通知</span>
        <el-button type="primary" link @click.stop="handleMarkAllRead">全部已读</el-button>
      </div>
      <div class="notification-list">
        <div v-if="notifications.length === 0" class="empty">暂无通知</div>
        <div v-for="item in notifications" :key="item.id" 
             :class="['notification-item', { unread: item.isRead === 0 }]"
             @click.stop="handleRead(item)">
          <div class="notification-title">{{ item.title }}</div>
          <div class="notification-content">{{ item.content }}</div>
          <div class="notification-time">{{ item.createTime }}</div>
        </div>
      </div>
      <div class="dropdown-footer">
        <el-button type="primary" link @click.stop="goToAll">查看全部</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { getNotifications, getUnreadCount, markAsRead, markAllAsRead } from '@/api/notification'

const router = useRouter()
const showDropdown = ref(false)
const unreadCount = ref(0)
const notifications = ref([])

const fetchData = async () => {
  try {
    const [countRes, listRes] = await Promise.all([
      getUnreadCount(),
      getNotifications({ page: 1, size: 5 })
    ])
    unreadCount.value = countRes.data.count || 0
    notifications.value = listRes.data.records || []
  } catch (e) {
    console.error(e)
  }
}

const handleRead = async (item) => {
  if (item.isRead === 0) {
    await markAsRead(item.id)
    item.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

const handleMarkAllRead = async () => {
  await markAllAsRead()
  notifications.value.forEach(n => n.isRead = 1)
  unreadCount.value = 0
}

const goToAll = () => {
  showDropdown.value = false
  router.push('/notifications')
}

const handleClickOutside = (e) => {
  if (!e.target.closest('.notification-bell')) {
    showDropdown.value = false
  }
}

onMounted(() => {
  fetchData()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.notification-bell {
  position: relative;
  cursor: pointer;
  padding: 8px;
}

.notification-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 320px;
  background: var(--mi-bg-card);
  border-radius: var(--mi-radius-sm);
  box-shadow: var(--mi-shadow-md);
  z-index: 1000;
  margin-top: 8px;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--mi-border-color);
  font-weight: 600;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.empty {
  padding: 24px;
  text-align: center;
  color: var(--mi-text-secondary);
}

.notification-item {
  padding: 12px 16px;
  border-bottom: 1px solid var(--mi-border-color);
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: var(--mi-bg-page);
}

.notification-item.unread {
  background: rgba(255, 105, 0, 0.05);
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.notification-content {
  font-size: 12px;
  color: var(--mi-text-secondary);
  margin-bottom: 4px;
}

.notification-time {
  font-size: 11px;
  color: var(--mi-text-placeholder);
}

.dropdown-footer {
  padding: 8px 16px;
  text-align: center;
  border-top: 1px solid var(--mi-border-color);
}
</style>
```

---

### Task 11: Add Export Buttons to Frontend Pages

**Covers:** Export UI integration

**Files:**
- Create: `frontend/src/api/export.js`
- Modify: `frontend/src/views/teacher/ScoreQuery.vue` (add export button)
- Modify: `frontend/src/views/student/MyScore.vue` (add export button)

- [ ] **Step 1: Create export API module**

```javascript
import request from '@/utils/request'

export function exportScores(params) {
  return request({
    url: '/export/scores',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function exportMyScores() {
  return request({
    url: '/export/my-scores',
    method: 'get',
    responseType: 'blob'
  })
}
```

- [ ] **Step 2: Add export button to ScoreQuery.vue**

In the template section, add an export button next to the search button:

```vue
<el-button type="success" @click="handleExport">
  <el-icon><Download /></el-icon> 导出Excel
</el-button>
```

In the script section, add the export handler:

```javascript
import { exportScores } from '@/api/export'
import { Download } from '@element-plus/icons-vue'

const handleExport = async () => {
  try {
    const res = await exportScores({
      courseId: searchForm.courseId,
      semester: searchForm.semester
    })
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '成绩报表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    console.error(e)
  }
}
```

- [ ] **Step 3: Add export button to MyScore.vue**

Similar to ScoreQuery.vue, add export button for students to export their own scores.

---

### Task 12: Add Routes and Menu Items

**Covers:** Navigation integration

**Files:**
- Modify: `frontend/src/router/index.js`
- Modify: `frontend/src/layout/index.vue`

- [ ] **Step 1: Add profile route**

```javascript
// In router/index.js, add to children array:
{
  path: 'profile',
  name: 'Profile',
  component: () => import('@/views/profile/ProfilePage.vue'),
  meta: { title: '个人信息' }
},
{
  path: 'notifications',
  name: 'Notifications',
  component: () => import('@/views/notification/NotificationList.vue'),
  meta: { title: '通知中心' }
}
```

- [ ] **Step 2: Add NotificationBell to layout**

In `layout/index.vue`, add the NotificationBell component to the topbar:

```vue
<script setup>
import NotificationBell from '@/components/NotificationBell.vue'
</script>

<template>
  <!-- In topbar-right, before the user dropdown -->
  <div class="topbar-right">
    <NotificationBell />
    <el-dropdown @command="handleCommand" trigger="click">
      <!-- existing user dropdown -->
    </el-dropdown>
  </div>
</template>
```

---

### Task 13: Rebuild and Test

**Covers:** Verification

- [ ] **Step 1: Rebuild backend**

Run: `cd backend && mvn clean package -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 2: Restart backend**

Kill old Java process and start new jar.

- [ ] **Step 3: Test profile API**

```bash
# Get profile
curl -H "Authorization: Bearer 1_admin" http://localhost:8080/api/profile

# Update profile
curl -X PUT -H "Authorization: Bearer 1_admin" -H "Content-Type: application/json" \
  -d '{"realName":"管理员","phone":"13800000000"}' \
  http://localhost:8080/api/profile

# Change password
curl -X PUT -H "Authorization: Bearer 1_admin" -H "Content-Type: application/json" \
  -d '{"oldPassword":"123456","newPassword":"123456"}' \
  http://localhost:8080/api/profile/password
```

- [ ] **Step 4: Test export API**

```bash
# Export all scores
curl -H "Authorization: Bearer 1_admin" http://localhost:8080/api/export/scores -o scores.xlsx
```

- [ ] **Step 5: Test notification API**

```bash
# Get notifications
curl -H "Authorization: Bearer 1_admin" http://localhost:8080/api/notifications

# Get unread count
curl -H "Authorization: Bearer 1_admin" http://localhost:8080/api/notifications/unread-count
```

- [ ] **Step 6: Frontend verification**

Open http://localhost:3000, login, and verify:
- Profile page accessible from sidebar or user dropdown
- Notification bell shows in topbar
- Export buttons work on score pages

---

## Summary

| Task | Description | Files Changed |
|------|-------------|---------------|
| 1 | Add EasyExcel dependency | pom.xml |
| 2 | Create Notification entity/mapper | Notification.java, NotificationMapper.java, schema.sql |
| 3 | Create NotificationService | NotificationService.java, NotificationServiceImpl.java |
| 4 | Create NotificationController | NotificationController.java |
| 5 | Create ProfileController | ProfileController.java |
| 6 | Create ExportController | ExportController.java |
| 7 | Add Excel annotations to Score | Score.java |
| 8 | Verify interceptor paths | (no changes needed) |
| 9 | Create ProfilePage.vue | ProfilePage.vue, profile.js |
| 10 | Create Notification components | NotificationBell.vue, notification.js |
| 11 | Add export buttons | export.js, ScoreQuery.vue, MyScore.vue |
| 12 | Add routes and menu | router/index.js, layout/index.vue |
| 13 | Rebuild and test | - |
