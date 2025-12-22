/*
 * Copyright (c) by lovrabet.com 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.permission;

import com.lovrabet.runtime.dao.mapper.SmartExternalDatasourceMapper;
import com.lovrabet.runtime.model.*;
import com.lovrabet.runtime.model.common.ErrorCode;
import com.lovrabet.runtime.model.common.GlobalException;
import com.lovrabet.runtime.model.common.GlobalParamsHolder;
import com.lovrabet.runtime.model.enums.CommonExceptionEnum;
import com.lovrabet.runtime.model.permit.Permit;
import com.lovrabet.runtime.model.service.middleware.ConfigProvider;
import com.lovrabet.runtime.service.*;
import com.lovrabet.runtime.service.app.IAppDomainMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.lovrabet.runtime.model.common.Constant.*;

/**
 * 智能应用权限校验
 *
 * @author yuntoo
 */
@Aspect
@Component
@Slf4j
public class LovrabetPermissionAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private SmartAppService appService;
    @Autowired
    private SmartPageService pageService;
    @Autowired
    private IDatasetService iDatasetService;
    @Autowired
    private PermitService permitService;
    @Autowired
    private ConfigProvider configProvider;
    @Autowired
    private SmartMenuService smartMenuService;
    @Autowired
    private SmartExternalDatasourceMapper smartExternalDatasourceMapper;
    @Autowired
    private IAppDomainMappingService appDomainMappingService;
    @Autowired
    private IUserSessionService userSessionService;



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<String, Object> buildMethodParamMap(String[] paramNames, Object methodArgs[]) {
        HashMap<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            paramMap.put(paramNames[i], methodArgs[i]);
        }

        return paramMap;
    }

    /**
     * 根据key的SPEL表达式获取value, 示例:@{[updateParam].getAppCode()}
     * @param spelExpression
     * @param methodParams
     * @return
     */
    private Object processMethodELParam(String spelExpression, Map<String, Object> methodParams) {
        try {
            if (StringUtils.isBlank(spelExpression)) {
                return "";
            }
            String expStr = spelExpression.substring(2, spelExpression.length() - 1);
            // SpelParserConfiguration, 允许访问私有属性、允许方法访问
            ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));
            Expression expression = parser.parseExpression(expStr);
            return expression.getValue(new StandardEvaluationContext(methodParams));
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid method el param pattern: " + spelExpression, e);
        }
    }

    private Long transertToLong(String keyValue) {
        GlobalException.ifTrueThenThrow(!StringUtils.isNumeric(keyValue), ErrorCode.YT_PARAMETER_INVALID, "");
        return Long.valueOf(keyValue);
    }

    private User getCurrentUser() {
        User user = userSessionService.getCurrentUser();
        boolean missingUser = null == user || null == user.getId();
        // GlobalException.ifTrueThenThrow(missingUser, ErrorCode.SMART_NOT_LOGGED_IN);
        return user;
    }

    /**
     * 是否需要跳过,yuntoo租户管理员则直接跳过
     * @return
     */
    private Boolean isNeedSkip() {
        boolean ytAdmin = userSessionService.isSuperAdmin();
        if (ytAdmin) {
            return true;
        }
        return false;
    }

    /**
     * 是否是租户的管理员
     * @return
     */
    private Boolean isSuperAdmin() {
        return true;
    }

    /**
     * 租户管理员
     * @param proJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.demoai.demo.lovrabet.permission.LovrabetSuperAdmin)")
    public Object checkSuperAdmin(ProceedingJoinPoint proJoinPoint) throws Throwable {
        if (isSuperAdmin()) {
            return proJoinPoint.proceed();
        }
        User user = getCurrentUser();
        String errMsg = String.format("权限不足,非租户管理员,method: %s, userId: %s", proJoinPoint.getSignature().getName(), user.getUserId());
        throw new GlobalException(errMsg, ErrorCode.NO_SUPER_ADMIN_PERMISSION, CommonExceptionEnum.NO_SUPER_ADMIN_PERMISSION);
    }

    /**
     * SmartAppPermission 权限处理逻辑
     * @param pjp 切点
     * @return 权限判断结果
     * @throws Throwable 抛出权限异常
     */
    @Around("@annotation(com.demoai.demo.lovrabet.permission.LovrabetPermission)")
    public Object checkPermission(ProceedingJoinPoint pjp) throws Throwable {
        User user = getCurrentUser();
        // 是否需要跳过
        if (isNeedSkip()) {
            return pjp.proceed();
        }

        // TODO 待补充权限校验逻辑，默认先通过

        return pjp.proceed();
    }

    /**
     * 根据注解上配置信息获取SmartApp对象
     * @param keyValue
     * @param resourceType
     * @return
     */
    private SmartApp getSmartApp(Object keyValue, PermitKeyTypeEnum resourceType) {
        GlobalException.ifTrueThenThrow(null == keyValue, ErrorCode.YT_PARAMETER_MISSING, "");
        String key = String.valueOf(keyValue);
        SmartApp smartApp = null;
        switch (resourceType) {
            case APP_CODE:
                smartApp = appService.findByAppCode(key);
                break;
            case MENU_ID:
                SmartMenu smartMenu = smartMenuService.findById(transertToLong(key));
                GlobalException.ifTrueThenThrow(null == smartMenu, ErrorCode.YT_PARAMETER_INVALID, "menuId");
                smartApp = appService.findByAppCode(smartMenu.getAppCode());
                break;
            case PAGE_ID:
                SmartPage smartPage = pageService.findById(transertToLong(key));
                GlobalException.ifTrueThenThrow(null == smartPage, ErrorCode.YT_PARAMETER_INVALID, "pageId");
                smartApp = appService.findByAppCode(smartPage.getAppCode());
                break;
            case DATASET_ID:
                Dataset dataset = iDatasetService.findById(transertToLong(key));
                GlobalException.ifTrueThenThrow(null == dataset, ErrorCode.YT_PARAMETER_INVALID, "datasetId");
                smartApp = appService.findByAppCode(dataset.getAppCode());
                break;
            case DATASET_CODE:
                Dataset datasetInfo = iDatasetService.findByCode(key);
                GlobalException.ifTrueThenThrow(null == datasetInfo, ErrorCode.YT_PARAMETER_INVALID, "datasetCode");
                smartApp = appService.findByAppCode(datasetInfo.getAppCode());
                break;
            case PERMIT_ID:
                Permit permit = permitService.find(transertToLong(key));
                GlobalException.ifTrueThenThrow(null == permit, ErrorCode.YT_PARAMETER_INVALID, "permitId");
                smartApp = appService.findByAppCode(permit.getAppCode());
                break;
            default:
                break;
        }
//        GlobalException.ifTrueThenThrow(null == smartApp, ErrorCode.YT_PARAMETER_INVALID, key, resourceType.name(), UserSessionHolder.getUser().getUserId());
        return smartApp;
    }

    /**
     * 只读白名单
     * @param keyValue
     * @param resourceType
     * @return
     */
    private boolean inReadOnlyWhiteList(Object keyValue, PermitKeyTypeEnum resourceType) {
        GlobalException.ifTrueThenThrow(null == keyValue, ErrorCode.YT_PARAMETER_MISSING, "");
        //只读白名单
        DataObject whiteListMap = configProvider.getConfig(DIAMOND_GROUP_LOW_CODE,
                SMART_APP_PERMISSION_READ_ONLY_WHITELIST, DataObject.class);
        String key = String.valueOf(keyValue);
        String keyListStr = (String)whiteListMap.get(resourceType.name());
        return Arrays.asList(keyListStr.split(COMMA)).contains(key);
    }

    /**
     * 根据appCode和originDomain查询appDomainMapping
     *
     * @param appCode 应用编码
     * @return 是否存在映射关系
     */
    private boolean checkAppDomainMapping(String appCode) {
        String originDomain = GlobalParamsHolder.getOriginDomain();
        // 如果originDomain为空，表示是同域请求或非跨域请求，直接通过校验
        if (StringUtils.isBlank(originDomain)) {
            return true;
        }
        // 跨域请求时，需要校验appCode和originDomain的映射关系是否存在
        return appDomainMappingService.countByAppCodeAndDomain(appCode, originDomain) > 0;
    }
}
