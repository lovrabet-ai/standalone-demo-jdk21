package com.demoai.demo.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.demoai.demo"})
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LovrabetDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LovrabetDemoApplication.class, args);
    }
}