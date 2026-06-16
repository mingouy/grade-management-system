package com.grade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.grade.entity.SysUser;

import java.util.Map;

/**
 * 用户Service接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token和用户信息
     */
    Map<String, Object> login(String username, String password);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
}
