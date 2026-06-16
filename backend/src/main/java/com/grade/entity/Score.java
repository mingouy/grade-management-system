package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@TableName("score")
public class Score {

    /**
     * 成绩ID
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
     * 成绩分数（0-100）
     */
    private BigDecimal score;

    /**
     * 学期
     */
    private String semester;

    /**
     * 考试类型（期末考试/期中考试/补考/重修）
     */
    private String examType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
    private BigDecimal credit;
}
