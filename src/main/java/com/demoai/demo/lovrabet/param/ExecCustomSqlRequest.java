/*
 * Copyright (c) by lovrabet.com 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.param;

import lombok.Data;

import java.util.Map;

/**
 * @author lixinwei
 * @date 2025/9/16 14:57
 * @description
 */
@Data
public class ExecCustomSqlRequest {
    /**
     * 自定义sql id
     */
    private Long id;

    /**
     * 自定义sql code
     */
    private String sqlCode;

    /**
     * 数据集id
     */
    private Long datasetId;

    /**
     * 自定义sql语句, 格式遵循MyBatis的参数化语法,
     * 示例：SELECT * FROM users WHERE id = #{userId} AND status = #{status}
     */
    private String sqlContent;

    /**
     * 自定义sql参数
     */
    private Map<String, Object> params;
}
