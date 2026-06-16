package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专业实体类
 */
@Data
@TableName("department")
public class Department {

    /**
     * 专业ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 专业名称
     */
    private String deptName;

    /**
     * 专业编码
     */
    private String deptCode;

    /**
     * 专业描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
