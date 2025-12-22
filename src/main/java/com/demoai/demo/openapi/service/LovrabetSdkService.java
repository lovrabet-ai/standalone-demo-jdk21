//package com.demoai.demo.openapi.service;
//
//import com.lovrabet.runtime.client.LovrabetSDKClient;
//import com.lovrabet.runtime.model.LovrabetRequest;
//import com.lovrabet.runtime.model.LovrabetResult;
//import org.springframework.stereotype.Component;
//
///**
// * @author lixinwei
// * @date 2025/9/25 11:29
// * @description
// */
//@Component
//public class LovrabetSdkService {
//    private static final String ACCESS_KEY = "ak-OD8xWhMOsL5ChQ3Akhv4uYYiu1fPOFQGVF9BULIeov8";
//
//    /**
//     * getOne接口
//     * @param request
//     * @return
//     */
//    public LovrabetResult<?> getOne(LovrabetRequest request) {
//        LovrabetSDKClient client = new LovrabetSDKClient(ACCESS_KEY);
//        return client.getOne(request);
//    }
//
//    /**
//     * createOne接口
//     * @param request
//     * @return
//     */
//    public LovrabetResult<?> create(LovrabetRequest request) {
//        LovrabetSDKClient client = new LovrabetSDKClient(ACCESS_KEY);
//        return client.create(request);
//    }
//
//    /**
//     * updateOne接口
//     * @param request
//     * @return
//     */
//    public LovrabetResult<?> update(LovrabetRequest request) {
//        LovrabetSDKClient client = new LovrabetSDKClient(ACCESS_KEY);
//        return client.update(request);
//    }
//
//    /**
//     * getDatasetList接口
//     * @param request
//     * @return
//     */
//    public LovrabetResult<?> getDatasetList(LovrabetRequest request) {
//        LovrabetSDKClient client = new LovrabetSDKClient(ACCESS_KEY);
//        return client.getDatasetList(request);
//    }
//
//    /**
//     * getList接口
//     * @param request
//     * @return
//     */
//    public LovrabetResult<?> getList(LovrabetRequest request) {
//        LovrabetSDKClient client = new LovrabetSDKClient(ACCESS_KEY);
//        return client.getList(request);
//    }
//
//    /**
//     * deleteOne接口
//     * @param request
//     * @return
//     */
//    public LovrabetResult<?> deleteOne(LovrabetRequest request) {
//        LovrabetSDKClient client = new LovrabetSDKClient(ACCESS_KEY);
//        return client.deleteOne(request);
//    }
//}
