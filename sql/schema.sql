-- ============================================================
-- 大学成绩管理系统 - 数据库建表脚本
-- 数据库：grade_management
-- 字符集：UTF8MB4
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS grade_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE grade_management;

-- ============================================================
-- 1. 角色表 sys_role
-- ============================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    `role_code`   VARCHAR(50)  NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================
-- 2. 用户表 sys_user
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    `real_name`   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    `role`        VARCHAR(20)  NOT NULL COMMENT '角色（admin/teacher/student）',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态（1-正常 0-禁用）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 3. 专业表 department
-- ============================================================
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '专业ID',
    `dept_name`   VARCHAR(100) NOT NULL COMMENT '专业名称',
    `dept_code`   VARCHAR(50)  NOT NULL COMMENT '专业编码',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '专业描述',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

-- ============================================================
-- 4. 班级表 class_info
-- ============================================================
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '班级ID',
    `class_name`  VARCHAR(100) NOT NULL COMMENT '班级名称',
    `class_code`  VARCHAR(50)  NOT NULL COMMENT '班级编码',
    `dept_id`     BIGINT       NOT NULL COMMENT '所属专业ID',
    `grade_year`  INT          NOT NULL COMMENT '年级（如2023）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_code` (`class_code`),
    KEY `idx_dept_id` (`dept_id`),
    CONSTRAINT `fk_class_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- ============================================================
-- 5. 教师信息表 teacher_info
-- ============================================================
DROP TABLE IF EXISTS `teacher_info`;
CREATE TABLE `teacher_info` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '教师信息ID',
    `user_id`     BIGINT       NOT NULL COMMENT '关联用户ID',
    `teacher_no`  VARCHAR(30)  NOT NULL COMMENT '教师工号',
    `name`        VARCHAR(50)  NOT NULL COMMENT '教师姓名',
    `gender`      VARCHAR(10)  NOT NULL COMMENT '性别（男/女）',
    `dept_id`     BIGINT       NOT NULL COMMENT '所属专业ID',
    `title`       VARCHAR(50)  DEFAULT NULL COMMENT '职称（教授/副教授/讲师/助教）',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_no` (`teacher_no`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_teacher_dept` (`dept_id`),
    CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
    CONSTRAINT `fk_teacher_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- ============================================================
-- 6. 学生信息表 student_info
-- ============================================================
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '学生信息ID',
    `user_id`         BIGINT       NOT NULL COMMENT '关联用户ID',
    `student_no`      VARCHAR(30)  NOT NULL COMMENT '学号',
    `name`            VARCHAR(50)  NOT NULL COMMENT '学生姓名',
    `gender`          VARCHAR(10)  NOT NULL COMMENT '性别（男/女）',
    `dept_id`         BIGINT       NOT NULL COMMENT '所属专业ID',
    `class_id`        BIGINT       NOT NULL COMMENT '所属班级ID',
    `enrollment_year` INT          NOT NULL COMMENT '入学年份',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_student_dept` (`dept_id`),
    KEY `idx_student_class` (`class_id`),
    CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
    CONSTRAINT `fk_student_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`),
    CONSTRAINT `fk_student_class` FOREIGN KEY (`class_id`) REFERENCES `class_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- ============================================================
-- 7. 课程表 course
-- ============================================================
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `course_code` VARCHAR(50)  NOT NULL COMMENT '课程编码',
    `credit`      DECIMAL(3,1) NOT NULL COMMENT '学分',
    `hours`       INT          NOT NULL COMMENT '学时',
    `teacher_id`  BIGINT       NOT NULL COMMENT '授课教师ID',
    `semester`    VARCHAR(20)  NOT NULL COMMENT '开课学期（如2024-2025-1）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_code` (`course_code`),
    KEY `idx_course_teacher` (`teacher_id`),
    CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- ============================================================
-- 8. 选课表 course_selection
-- ============================================================
DROP TABLE IF EXISTS `course_selection`;
CREATE TABLE `course_selection` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '选课ID',
    `student_id`   BIGINT      NOT NULL COMMENT '学生信息ID',
    `course_id`    BIGINT      NOT NULL COMMENT '课程ID',
    `semester`     VARCHAR(20) NOT NULL COMMENT '选课学期',
    `select_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_selection` (`student_id`, `course_id`, `semester`),
    KEY `idx_selection_course` (`course_id`),
    CONSTRAINT `fk_selection_student` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`id`),
    CONSTRAINT `fk_selection_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

-- ============================================================
-- 9. 成绩表 score
-- ============================================================
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
    `student_id`  BIGINT       NOT NULL COMMENT '学生信息ID',
    `course_id`   BIGINT       NOT NULL COMMENT '课程ID',
    `score`       DECIMAL(5,2) DEFAULT NULL COMMENT '成绩分数（0-100）',
    `semester`    VARCHAR(20)  NOT NULL COMMENT '学期',
    `exam_type`   VARCHAR(20)  NOT NULL DEFAULT '期末考试' COMMENT '考试类型（期末考试/期中考试/补考/重修）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_score` (`student_id`, `course_id`, `exam_type`),
    KEY `idx_score_course` (`course_id`),
    CONSTRAINT `fk_score_student` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`id`),
    CONSTRAINT `fk_score_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';
