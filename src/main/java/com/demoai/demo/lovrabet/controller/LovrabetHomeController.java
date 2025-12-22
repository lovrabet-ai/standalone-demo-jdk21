package com.demoai.demo.lovrabet.controller;

import com.demoai.demo.lovrabet.config.LovrabetConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 类HomeController的描述: 通用控制器，负责静态资源的加载
 * <br/>
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2025/9/17 09:48
 */
@Slf4j
@Controller
public class LovrabetHomeController {
    private final LovrabetConfigProperties lovrabetConfig;

    public LovrabetHomeController(LovrabetConfigProperties lovrabetConfig) {
        this.lovrabetConfig = lovrabetConfig;
    }

    @GetMapping("/**")
    public String index(Model model) {
        model.addAttribute("appCode", lovrabetConfig.getAppCode());
        model.addAttribute("apiDomain", lovrabetConfig.getApiDomain());
        model.addAttribute("rendererVersion", lovrabetConfig.getRendererVersion());
        model.addAttribute("appPlatformVersion", lovrabetConfig.getAppPlatformVersion());
        log.debug("appCode:{}, apiDomain:{}", lovrabetConfig.getAppCode(), lovrabetConfig.getApiDomain());
        return "index"; // 自动解析为 templates/index.html
    }
}
