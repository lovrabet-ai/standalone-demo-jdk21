/*
 * Copyright (c) by yuntoo.ai 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.controller;

import com.demoai.demo.lovrabet.permission.LovrabetPermission;
import com.lovrabet.runtime.model.PermitKeyTypeEnum;
import com.lovrabet.runtime.model.SmartPageDetail;
import com.lovrabet.runtime.model.common.Result;
import com.lovrabet.runtime.service.SmartPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 云兔页面控制器
 */
@Api(tags = "页面")
@RestController
@RequestMapping(value = "/api/page")
public class LovrabetPageController {

    @Resource
    private SmartPageService smartPageService;

    /**
     * 根据id获取页面详情及数据集详情
     */
    @GetMapping("/detail")
    @ApiOperation("获取页面详情")
    @LovrabetPermission(key = "@{[pageId]}", keyType = PermitKeyTypeEnum.PAGE_ID)
    public Result<SmartPageDetail> pageDetail(@RequestParam(value = "pageId") Long pageId) {
        return Result.success(smartPageService.getAdvanceDetail(pageId), "查询成功");
    }
}