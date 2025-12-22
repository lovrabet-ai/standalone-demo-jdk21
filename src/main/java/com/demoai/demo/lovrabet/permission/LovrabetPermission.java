/*
 * Copyright (c) by yuntoo.ai 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.permission;

import com.lovrabet.runtime.model.PermitKeyTypeEnum;
import com.lovrabet.runtime.model.enums.AuditStatusEnum;

import java.lang.annotation.*;

/**
 * 云兔权限
 *
 * @author yuntoo
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LovrabetPermission {
    /**
     * key 取值方式 匹配@{}格式,获取方法入参,支持SpEL表达式.由于内部使用map存储方法入参,需使用[]包装参数
     * 比如：com.yuntoo.smartcode.smart.controller.SmartAppController#transmitOwner(com.yuntoo.smartcode.smart.controller.param.SmartAppTransmitOwnerParam)
     *
     * @return
     */
    String key() default "";

    /**
     * key所属的资源类型，默认智能应用
     */
    PermitKeyTypeEnum keyType() default PermitKeyTypeEnum.APP_CODE;

    /**
     * 应用负责人的鉴权方式 PASS 直接通过 AUDIT 需要审批 DENIED 拒绝 NULL
     *
     * @return
     */
    AuditStatusEnum ownerAudit() default AuditStatusEnum.PASS;

    /**
     * 应用管理员的鉴权方式 PASS 直接通过 AUDIT 需要审批 DENIED 拒绝 NULL
     *
     * @return
     */
    AuditStatusEnum adminAudit() default AuditStatusEnum.PASS;

    /**
     * 普通成员的鉴权方式 PASS 直接通过 AUDIT 需要审批 DENIED 拒绝 NULL
     *
     * @return
     */
    AuditStatusEnum developerAudit() default AuditStatusEnum.PASS;

    /**
     * 是否允许只读
     *
     * @return
     */
    boolean allowReadOnly() default false;
}
