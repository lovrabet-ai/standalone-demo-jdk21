/*
 * Copyright (c) by lovrabet.com 2025-2035. All right reserved.
 */

package com.demoai.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 类WebConfig的描述: web配置
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2026/2/2 10:38
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 为所有控制器添加统一的前缀，比如：/demo
        //configurer.addPathPrefix("/demo", clazz -> clazz.isAnnotationPresent(RestController.class));

        // 如果只想为特定包下的控制器添加前缀
        // configurer.addPathPrefix("/demo", clazz -> clazz.getPackageName().startsWith("com.example.api"));
    }
}
