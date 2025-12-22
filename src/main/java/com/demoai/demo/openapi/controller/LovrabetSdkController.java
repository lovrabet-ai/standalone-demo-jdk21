//package com.demoai.demo.openapi.controller;
//
//import com.demoai.demo.openapi.service.LovrabetSdkService;
//import com.lovrabet.runtime.model.LovrabetRequest;
//import com.lovrabet.runtime.model.LovrabetResult;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author lixinwei
// * @date 2025/9/25 11:54
// * @description
// */
//@RestController
//@RequestMapping("/openApi")
//@RequiredArgsConstructor
//public class LovrabetSdkController {
//    @Autowired
//    private LovrabetSdkService lovrabetSdkService;
//
//    /**
//     * getOne接口测试
//     * @param request
//     * @return
//     */
//    @RequestMapping("/getOne")
//    public LovrabetResult<?> getOne(@RequestBody LovrabetRequest request) {
//        return lovrabetSdkService.getOne(request);
//    }
//
//    /**
//     * getDatasetList接口测试
//     * @param request
//     * @return
//     */
//    @RequestMapping("/getDatasetList")
//    public LovrabetResult<?> getDatasetList(@RequestBody LovrabetRequest request) {
//        return lovrabetSdkService.getDatasetList(request);
//    }
//    /**
//     * getList接口测试
//     * @param request
//     * @return
//     */
//    @RequestMapping("/getList")
//    public LovrabetResult<?> getList(@RequestBody LovrabetRequest request) {
//        return lovrabetSdkService.getList(request);
//    }
//}
