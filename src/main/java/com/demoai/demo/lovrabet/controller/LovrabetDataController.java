/*
 * Copyright (c) by yuntoo.ai 2025-2035. All Rights Reserved.
 */

package com.demoai.demo.lovrabet.controller;

import com.alibaba.fastjson2.JSONObject;
import com.demoai.demo.lovrabet.permission.LovrabetPermission;
import com.lovrabet.runtime.model.PermitKeyTypeEnum;
import com.lovrabet.runtime.model.common.Result;
import com.lovrabet.runtime.model.enums.DatasetOperationTypeEnum;
import com.lovrabet.runtime.service.SmartDataDispatcher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 云兔数据操作控制器
 */
@Api(tags = "数据操作")
@RestController
@RequestMapping("/api")
@Slf4j
public class LovrabetDataController {
    @Resource
    SmartDataDispatcher smartDataDispatcher;

    @ApiOperation("数据操作-getOne")
    @PostMapping("/{appCode}/{datasetCode}/getOne")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> getOne(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                            @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.GET_ONE, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.GET_ONE, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.GET_ONE, paramMap);
        return result;
    }

    @ApiOperation("数据操作-getList")
    @PostMapping("/{appCode}/{datasetCode}/getList")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> getList(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                            @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.GET_LIST, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.GET_LIST, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.GET_LIST, paramMap);
        return result;
    }

    @ApiOperation("数据操作-getOneOrigin")
    @PostMapping("/{appCode}/{datasetCode}/getOneOrigin")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> getOneOrigin(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                                  @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.GET_ONE_ORIGIN, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.GET_ONE_ORIGIN, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.GET_ONE_ORIGIN, paramMap);
        return result;
    }

    @ApiOperation("数据操作-create")
    @PostMapping("/{appCode}/{datasetCode}/create")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> create(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                                  @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.CREATE, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.CREATE, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.CREATE, paramMap);
        return result;
    }

    @ApiOperation("数据操作-update")
    @PostMapping("/{appCode}/{datasetCode}/update")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> update(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                            @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.UPDATE, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.UPDATE, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.UPDATE, paramMap);
        return result;
    }

    @ApiOperation("数据操作-getSelectOptions")
    @PostMapping("/{appCode}/{datasetCode}/getSelectOptions")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> getSelectOptions(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                            @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.GET_SELECT_OPTIONS, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.GET_SELECT_OPTIONS, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.GET_SELECT_OPTIONS, paramMap);
        return result;
    }

    @ApiOperation("数据操作-delete")
    @PostMapping("/{appCode}/{datasetCode}/delete")
    @LovrabetPermission(key = "@{[appCode]}", keyType = PermitKeyTypeEnum.APP_CODE)
    public Result<?> delete(@PathVariable("appCode") String appCode, @PathVariable("datasetCode") String datasetCode,
                                      @RequestBody Map<String, Object> paramMap) {
        // 检查请求参数
        smartDataDispatcher.checkPageParam(paramMap, DatasetOperationTypeEnum.DELETE, false);
        Result<?> result =  smartDataDispatcher.dataExecute(datasetCode, appCode, DatasetOperationTypeEnum.DELETE, paramMap);
        logResult(appCode, datasetCode, DatasetOperationTypeEnum.DELETE, paramMap);
        return result;
    }

    private void logResult(String appCode, String datasetCode, DatasetOperationTypeEnum operationType,
                           Map<String, Object> paramMap) {
        log.info("data execute success, appCode:{}, datasetCode:{}, operationType:{}, param:{}",
                appCode, datasetCode, operationType, JSONObject.toJSONString(paramMap));
    }
}
