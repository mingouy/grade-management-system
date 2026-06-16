package com.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 教师信息实体类
 */
@Data
@TableName("teacher_info")
public class TeacherInfo {

    /**
     * 教师信息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 教师工号
     */
    private String teacherNo;

    /**
     * 教师姓名
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
     * 职称（教授/副教授/讲师/助教）
     */
    private String title;

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
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;
}
