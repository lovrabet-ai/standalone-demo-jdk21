/*
 * Copyright (c) by lovrabet.com 2025-2035. All right reserved.
 */

package com.demoai.demo.service.impl;

import com.lovrabet.runtime.model.common.PageResult;
import com.lovrabet.runtime.model.dto.YtDeptRequest;
import com.lovrabet.runtime.model.dto.YtEmployee;
import com.lovrabet.runtime.service.IUserEmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 类UserEmployeeServiceImpl的描述: 用户员工信息Service的实现类，如果用到了员工选择器，需要自定义扩展实现
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2026/3/17 11:38
 */
@Service
public class UserEmployeeServiceImpl implements IUserEmployeeService {

    @Override
    public YtEmployee getById(String id) {
        if (!StringUtils.isNumeric(id)) {
            return null;
        }

        // 根据用户id等标识返回用户信息，此为demo，需要自定义扩展实现
        return new YtEmployee();
    }

    @Override
    public List<YtEmployee> getUserEmployeeByIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据用户id等标识列表返回用户信息列表，此为demo，需要自定义扩展实现
        return new ArrayList<>();
    }

    @Override
    public PageResult<YtEmployee> getUserListWithPaging(YtDeptRequest request) {
        if (request == null) {
            return new PageResult<>();
        }

        // 分页查询用户列表列表，此为demo，需要自定义扩展实现
        return PageResult.of(new ArrayList<>(), 1, 10, 100);
    }

    @Override
    public String getUserCodeByInput(String input) {
        // 根据输入返回用户标识，此为demo，需要自定义扩展实现
        return "";
    }
}
