/*
 * Copyright (c) by yuntoo.ai 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.controller;

import com.demoai.demo.lovrabet.permission.LovrabetPermission;
import com.lovrabet.runtime.model.SmartApp;
import com.lovrabet.runtime.model.common.Result;
import com.lovrabet.runtime.service.SmartAppService;
import io.swagger.annotations.Api;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用Controller
 */
@Slf4j
@Api(tags = "智能应用--应用")
@RestController
@RequestMapping("/api/app")
public class LovrabetAppController {
    @Resource
    private SmartAppService smartAppService;

    @GetMapping("/getOne")
    @LovrabetPermission(key = "@{[appCode]}")
    public Result<SmartApp> getOne(@RequestParam String appCode) {
        SmartApp smartApp = smartAppService.findByAppCode(appCode);
        if(null == smartApp){
            return Result.success(null, "应用不存在或者无效的appCode");
        }
        return Result.success(smartApp, "查询成功");
    }
}
