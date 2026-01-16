package com.demoai.demo.lovrabet.controller;

import com.demoai.demo.lovrabet.param.ExecCustomSqlRequest;
import com.demoai.demo.lovrabet.permission.LovrabetPermission;
import com.lovrabet.runtime.model.PermitKeyTypeEnum;
import com.lovrabet.runtime.model.common.DataList;
import com.lovrabet.runtime.model.common.ErrorCode;
import com.lovrabet.runtime.model.common.Result;
import com.lovrabet.runtime.model.common.SqlExecResult;
import com.lovrabet.runtime.service.IUserCustomSqlService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * @author lixinwei
 * @date 2025/9/16 14:08
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/api/custom")
public class LovrabetCustomSqlController {
    @Resource
    private IUserCustomSqlService userCustomSqlService;

    /**
     * 执行用户自定义SQL
     * @param request 执行请求
     * @return 执行结果
     */
    @PostMapping("/executeSql")
    @LovrabetPermission(key = "@{[request].getSqlCode()}", keyType = PermitKeyTypeEnum.SQL_CODE)
    Result<SqlExecResult> executeUserCustomSql(@RequestBody ExecCustomSqlRequest request) {
        try {
            SqlExecResult sqlExecResult = userCustomSqlService.executeCustomSql(request.getId(), request.getSqlCode(),
                    request.getParams(), false);
            return Result.success(sqlExecResult);
        } catch (IllegalArgumentException e) {
            ErrorCode failure = ErrorCode.PARAM_INVALID;
            return Result.error(failure, e.getMessage());
        } catch (Exception e){
            ErrorCode failure = ErrorCode.YT_FAILURE;
            return Result.error(failure, e.getMessage());
        }
    }

    /**
     * 执行自定义sql,携带业务含义,对sql语句进行适当调整
     *
     * @param request 数据库链接相关的参数
     * @return 执行结果
     */
    @PostMapping("/execPreparedSqlX")
    @LovrabetPermission(key = "@{[request].getDatasetId()}", keyType = PermitKeyTypeEnum.DATASET_ID)
    Result<DataList<Map<String, Object>>> execCustomPreparedSqlX(@RequestBody ExecCustomSqlRequest request){
        if (null == request || Objects.isNull(request.getDatasetId())
                || StringUtils.isBlank(request.getSqlContent()) || Objects.isNull(request.getParams())) {
            ErrorCode failure = ErrorCode.PARAM_MISSING;
            return Result.error(failure, "参数缺失");
        }
        try {
            DataList<Map<String, Object>> mapDataList = userCustomSqlService.execCustomPreparedSqlX(request.getDatasetId(),
                    null, request.getSqlContent(), request.getParams());
            return Result.success(mapDataList);
        } catch (Exception e) {
            ErrorCode failure = ErrorCode.YT_FAILURE;
            return Result.error(failure, e.getMessage());
        }
    }

}
