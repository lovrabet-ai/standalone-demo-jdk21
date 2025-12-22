/*
 * Copyright (c) by yuntoo.ai 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.controller;

import com.demoai.demo.lovrabet.permission.LovrabetPermission;
import com.lovrabet.runtime.model.SmartMenu;
import com.lovrabet.runtime.model.common.Result;
import com.lovrabet.runtime.service.SmartMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 云兔菜单控制器
 */
@Api(tags = "菜单")
@RestController
@RequestMapping(value = "/api/menu")
@Slf4j
public class LovrabetMenuController {
    @Autowired
    private SmartMenuService smartMenuService;

    @GetMapping(value = "/find")
    @ApiOperation("查询菜单树")
    @LovrabetPermission(key = "@{[appCode]}")
    public Result<List<SmartMenu>> find(@RequestParam String appCode) {
        SmartMenu param = new SmartMenu();
        param.setAppCode(appCode);
        param.setDeleted(false);
        List<SmartMenu> list = smartMenuService.list(param);
        SmartMenu smartMenu = smartMenuService.buildMenuTree(list);
        return Result.success(smartMenu.getChildren());
    }

}
