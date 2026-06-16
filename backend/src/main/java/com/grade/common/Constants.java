package com.grade.common;

/**
 * 系统常量
 */
public class Constants {

    /**
     * Token请求头名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 管理员角色
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * 教师角色
     */
    public static final String ROLE_TEACHER = "teacher";

    /**
     * 学生角色
     */
    public static final String ROLE_STUDENT = "student";

    /**
     * 用户状态：正常
     */
    public static final Integer STATUS_NORMAL = 1;

    /**
     * 用户状态：禁用
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 默认分页页码
     */
    public static final Long DEFAULT_PAGE = 1L;

    /**
     * 默认分页大小
     */
    public static final Long DEFAULT_SIZE = 10L;
}
