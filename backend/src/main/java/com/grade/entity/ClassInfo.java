package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级实体类
 */
@Data
@TableName("class_info")
public class ClassInfo {

    /**
     * 班级ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级编码
     */
    private String classCode;

    /**
     * 所属专业ID
     */
    private Long deptId;

    /**
     * 年级（如2023）
     */
    private Integer gradeYear;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 专业名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;
}
