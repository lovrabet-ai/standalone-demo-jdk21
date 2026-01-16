package com.demoai.demo.lovrabet.controller;

import com.alibaba.fastjson2.JSONObject;
import com.demoai.demo.lovrabet.permission.LovrabetPermission;
import com.lovrabet.runtime.core.service.script.ScriptEndpointExecutionService;
import com.lovrabet.runtime.model.User;
import com.lovrabet.runtime.model.common.Result;
import com.lovrabet.runtime.service.IUserSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 独立脚本端点控制器
 * 用于直接通过 HTTP 请求调用类型为 ENDPOINT 的脚本
 */
@Api(tags = "脚本端点")
@RestController
@RequestMapping("/api")
@Slf4j
public class LovrabetBfScriptController {

    @Resource
    private IUserSessionService userSessionService;

    @Resource
    private ScriptEndpointExecutionService scriptEndpointExecutionService;

    @Deprecated
    @ApiOperation("执行独立脚本，待废弃")
    @PostMapping("/{appCode}/endpoint/{scriptName}")
    @LovrabetPermission(key = "@{[appCode]}")
    public Result<?> executeEndpoint(@PathVariable("appCode") String appCode,
                                     @PathVariable("scriptName") String scriptName,
                                     @RequestBody(required = false) Map<String, Object> params) {
        
        log.info("Execute endpoint script: appCode={}, scriptName={}, params={}", appCode, scriptName, JSONObject.toJSONString(params));

        User currentUser = userSessionService.getCurrentUser();

        // 委托给 Service 执行，Service 内部负责事务管理和跨库检测
        Object result = scriptEndpointExecutionService.execute(appCode, scriptName, params, currentUser);

        return Result.success(result);
    }

    @ApiOperation("执行独立脚本")
    @PostMapping("/endpoint/{appCode}/{scriptName}")
    @LovrabetPermission(key = "@{[appCode]}")
    public Result<?> executeBfEndpoint(@PathVariable("appCode") String appCode,
                                     @PathVariable("scriptName") String scriptName,
                                     @RequestBody(required = false) Map<String, Object> params) {

        log.info("Execute endpoint script: appCode={}, scriptName={}, params={}", appCode, scriptName, JSONObject.toJSONString(params));

        User currentUser = userSessionService.getCurrentUser();

        // 委托给 Service 执行，Service 内部负责事务管理和跨库检测
        Object result = scriptEndpointExecutionService.execute(appCode, scriptName, params, currentUser);

        return Result.success(result);
    }
}
