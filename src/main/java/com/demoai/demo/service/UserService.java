package com.demoai.demo.service;

import com.demoai.demo.model.user.Role;
import com.demoai.demo.model.user.User;
import org.springframework.stereotype.Service;

/**
 * 类UserService的描述: 用户服务
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2025/9/12 11:24
 */
@Service
public class UserService {

    public User getByUsername(String username) {
        // 模拟从数据库中获取用户信息
        return new User(1001L, "yuntoo","云兔", "yuntoo@yuntoo-inc.com", "123456", Role.ADMIN);
    }

    public boolean save(User user) {
        return true;
    }
}
