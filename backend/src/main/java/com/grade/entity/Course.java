package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Data
@TableName("course")
public class Course {

    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编码
     */
    private String courseCode;

    /**
     * 学分
     */
    private BigDecimal credit;

    /**
     * 学时
     */
    private Integer hours;

    /**
     * 授课教师ID
     */
    private Long teacherId;

    /**
     * 开课学期（如2024-2025-1）
     */
    private String semester;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 教师姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String teacherName;

    /**
     * 教师工号（非数据库字段）
     */
    @TableField(exist = false)
    private String teacherNo;
}
