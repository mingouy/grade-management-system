package com.grade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grade.common.Constants;
import com.grade.entity.SysUser;
import com.grade.exception.BusinessException;
import com.grade.mapper.SysUserMapper;
import com.grade.service.SysUserService;
import cn.hutool.crypto.digest.MD5;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户Service实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 校验密码（MD5加密比对）
        String encryptPwd = MD5.create().digestHex(password);
        if (!encryptPwd.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 校验状态
        if (Constants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成token（简化实现：userId_role格式）
        String token = user.getId() + "_" + user.getRole();

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        // 脱敏处理
        user.setPassword(null);
        result.put("userInfo", user);

        return result;
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }
}
