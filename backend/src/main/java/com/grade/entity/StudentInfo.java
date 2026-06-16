package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学生信息实体类
 */
@Data
@TableName("student_info")
public class StudentInfo {

    /**
     * 学生信息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 性别（男/女）
     */
    private String gender;

    /**
     * 所属专业ID
     */
    private Long deptId;

    /**
     * 所属班级ID
     */
    private Long classId;

    /**
     * 入学年份
     */
    private Integer enrollmentYear;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 专业名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 班级名称（非数据库字段）
     */
    @TableField(exist = false)
    private String className;

    /**
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;
}
