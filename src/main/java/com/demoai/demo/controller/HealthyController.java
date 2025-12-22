package com.demoai.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类YtHealthyController的描述: 健康检查Controller类
 * <br/>

 * @author yuntoo
 * @date 2025/4/22
 */
@RestController
public class HealthyController {

    /**
     * 健康检查，系统部署需要
     */
    @GetMapping("/heartbeat")
    public @ResponseBody String heartbeat() {
        return "success";
    }
}
