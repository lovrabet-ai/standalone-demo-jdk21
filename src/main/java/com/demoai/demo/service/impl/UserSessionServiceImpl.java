package com.demoai.demo.service.impl;

import com.lovrabet.runtime.model.User;
import com.lovrabet.runtime.service.IUserSessionService;
import org.springframework.stereotype.Service;

/**
 * 类UserSessionServiceImpl的描述: 获取用户信息
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
        return new User().setUserId(1001L).setUsername("yuntoo").setName("云兔").setTenantCode("yuntoo");
    }

    @Override
    public boolean isSuperAdmin() {
        return false;
    }
}
