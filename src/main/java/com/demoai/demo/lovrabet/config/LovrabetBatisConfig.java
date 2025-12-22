package com.demoai.demo.lovrabet.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类MyBatisConfig的描述: mybatis配置，适配springboot3
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2025/6/7 2:23 PM
 */
@Configuration
public class LovrabetBatisConfig {
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            // 新版MyBatis直接通过Configuration设置
            configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        };
    }

    /**
     * 方法描述： mybatis-plus需要手动设置分页插件
     **/
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件（DbType根据实际数据库调整）
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(200L); // 单页最大记录数限制
        paginationInnerInterceptor.setOverflow(true);  // 超出页数返回第一页

        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
