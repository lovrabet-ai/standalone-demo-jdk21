/*
 * Copyright (c) by lovrabet.com 2025-2035. All right reserved.
 */

package com.demoai.demo.service.impl;

import com.lovrabet.runtime.model.User;
import com.lovrabet.runtime.service.IUserSessionService;
import org.springframework.stereotype.Service;

/**
 * 类UserSessionServiceImpl的描述: 获取用户和员工信息，独立部署需要自定义扩展实现
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2025/9/15 15:21
 */
@Service
public class UserSessionServiceImpl implements IUserSessionService {
    @Override
    public User getCurrentUser() {
        // 获取当前登录的用户，此为demo，需要自定义扩展实现
        return new User().setUserId(1001L).setUsername("yuntoo").setName("云兔").setTenantCode("yuntoo");
    }

    @Override
    public boolean isSuperAdmin() {
        // 是否超管，用户权限校验，此为demo，需要自定义扩展实现
        return false;
    }

    @Override
    public String getUserIdentity() {
        // 保存到业务表的当前用户标识，一般是返回用户的Id或者工号，此为demo，需要自定义扩展实现
        return "";
    }
}
