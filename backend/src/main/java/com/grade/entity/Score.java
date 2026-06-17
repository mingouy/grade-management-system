package com.grade.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生信息ID
     */
    @ExcelIgnore
    private Long studentId;

    /**
     * 课程ID
     */
    @ExcelIgnore
    private Long courseId;

    /**
     * 学号（非数据库字段）
     */
    @ExcelProperty("学号")
    @TableField(exist = false)
    private String studentNo;

    /**
     * 学生姓名（非数据库字段）
     */
    @ExcelProperty("学生姓名")
    @TableField(exist = false)
    private String studentName;

    /**
     * 课程编码（非数据库字段）
     */
    @ExcelProperty("课程编码")
    @TableField(exist = false)
    private String courseCode;

    /**
     * 课程名称（非数据库字段）
     */
    @ExcelProperty("课程名称")
    @TableField(exist = false)
    private String courseName;

    /**
     * 学分（非数据库字段）
     */
    @ExcelProperty("学分")
    @TableField(exist = false)
    private BigDecimal credit;

    /**
     * 成绩分数（0-100）
     */
    @ExcelProperty("成绩")
    private BigDecimal score;

    /**
     * 学期
     */
    @ExcelProperty("学期")
    private String semester;

    /**
     * 考试类型（期末考试/期中考试/补考/重修）
     */
    @ExcelProperty("考试类型")
    private String examType;

    /**
     * 创建时间
     */
    @ExcelIgnore
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelIgnore
    private LocalDateTime updateTime;
}
