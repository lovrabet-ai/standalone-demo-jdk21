package com.demoai.demo.lovrabet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类AppConfigProperties的描述: 应用配置
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2025/9/16 20:36
 */
@Data
@Component
@ConfigurationProperties(prefix = "lovrabet")
public class LovrabetConfigProperties {

    /**
     * Lovrabet应用编码
     */
    private String appCode;

    /**
     * API请求的域名
     */
    private String apiDomain;

    /**
     * 静态资源渲染版本号
     */
    private String rendererVersion;

    /**
     * 应用平台资源版本号
     */
    private String appPlatformVersion;
}
