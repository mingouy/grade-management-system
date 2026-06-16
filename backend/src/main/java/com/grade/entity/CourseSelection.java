package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 选课实体类
 */
@Data
@TableName("course_selection")
public class CourseSelection {

    /**
     * 选课ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生信息ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 选课学期
     */
    private String semester;

    /**
     * 选课时间
     */
    private LocalDateTime selectTime;

    /**
     * 学生姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String studentName;

    /**
     * 学号（非数据库字段）
     */
    @TableField(exist = false)
    private String studentNo;

    /**
     * 课程名称（非数据库字段）
     */
    @TableField(exist = false)
    private String courseName;

    /**
     * 课程编码（非数据库字段）
     */
    @TableField(exist = false)
    private String courseCode;

    /**
     * 学分（非数据库字段）
     */
    @TableField(exist = false)
    private java.math.BigDecimal credit;
}
