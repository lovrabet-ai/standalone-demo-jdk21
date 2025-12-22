/*
 * Copyright (c) by yuntoo.ai 2025-2035. All right reserved.
 */

package com.demoai.demo.lovrabet.exception;

import com.alibaba.fastjson2.JSONObject;
import com.lovrabet.runtime.model.common.ErrorCode;
import com.lovrabet.runtime.model.common.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;

/**
 * 类GlobalExceptionHandler的描述: 通用异常处理类
 * <br/>
 *
 * @author yuntoo
 * @version 1.0.0
 * @date 2025/4/22
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 无权限错误码
    private static final Set<ErrorCode> NO_PERMISSION_ERROR_CODES =
            Set.of(ErrorCode.NO_SUPER_ADMIN_PERMISSION,
                    ErrorCode.YT_NO_PERMISSION,
                    ErrorCode.DEVELOPER_NO_PERMISSION);

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGlobalException(GlobalException ex) {
        String errMsg = Optional.ofNullable(ex.getErrorMsg())
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> {
                    // 若有参数，转为JSON字符串；否则返回默认消息
                    if (Objects.nonNull(ex.getParams())) {
                        return JSONObject.toJSONString(ex.getParams());
                    }
                    return "业务处理失败";
                });
        log.error("业务异常: [{}], 错误码: [{}]", errMsg, ex.getErrorCode().getCode(), ex);
        // 判断是否为权限不足异常，直接通过条件赋值状态码
        boolean isNoPermission = NO_PERMISSION_ERROR_CODES.contains(ex.getErrorCode());
        HttpStatus httpStatus = isNoPermission ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;
        // 若为权限不足，强制覆盖错误消息（保持原业务逻辑）
        if (isNoPermission) {
            errMsg = "权限不足";
        }
        return buildErrorResponse(httpStatus, ex.getErrorCode().getCode(), errMsg);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数验证异常：{}", e.getMessage(), e);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "参数错误", e.getMessage());
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler({
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MethodArgumentNotValidException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleParameterBindingException(Exception e) {
        // 记录详细的参数绑定异常信息
        recordParameterBindingExceptionDetails(e);

        // 返回统一的错误响应
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "参数错误", "请求参数不正确：" + e.getMessage());
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("资源未找到：{}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "资源未找到", e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        if (e instanceof AccessDeniedException) {
            log.warn("权限不足：{}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "权限不足，不可访问", "非管理员不可访问");
        }else if (e instanceof EvaluationException || e instanceof ParseException) {
            log.warn("业务规则解析报错：{}", e.getMessage(), e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "业务规则解析报错",
                    "业务规则解析报错，请核查属性的类型设置以及操作符是否匹配");
        }
        log.error("运行时异常：{}", e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统内部错误", "服务暂时不可用，请联系技术人员");
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("全局异常捕获：", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统异常", "服务出现异常，请联系管理员");
    }

    /**
     * 记录参数绑定异常的详细信息
     */
    private void recordParameterBindingExceptionDetails(Exception e) {
        try {
            // 获取请求信息
            ParameterBindingRequestInfo requestInfo = getParameterBindingRequestInfo();

            // 获取异常调用栈
            String stackTrace = getParameterBindingStackTrace(e);

            // 构建详细的日志信息
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n=== 参数绑定异常监控 (GlobalExceptionHandler) ===");
            logMessage.append("\n异常类型: ").append(e.getClass().getSimpleName());
            logMessage.append("\n异常消息: ").append(e.getMessage());
            logMessage.append("\n异常阶段: 参数绑定/验证阶段");

            // 添加请求信息
            if (requestInfo != null) {
                logMessage.append("\n请求URL: ").append(requestInfo.getUrl());
                logMessage.append("\n请求方法: ").append(requestInfo.getMethod());
                logMessage.append("\n客户端IP: ").append(requestInfo.getRemoteAddr());
                logMessage.append("\n查询参数: ").append(requestInfo.getQueryString());
                logMessage.append("\nContent-Type: ").append(requestInfo.getContentType());
                logMessage.append("\nUser-Agent: ").append(requestInfo.getUserAgent());
                logMessage.append("\nReferer: ").append(requestInfo.getReferer());

                // 添加请求头信息
                if (requestInfo.getHeaders() != null && !requestInfo.getHeaders().isEmpty()) {
                    logMessage.append("\n请求头信息:");
                    requestInfo.getHeaders()
                            .forEach((key, value) -> logMessage.append("\n  ").append(key).append(": ").append(value));
                }

                // 添加请求参数
                if (requestInfo.getParameters() != null && !requestInfo.getParameters().isEmpty()) {
                    logMessage.append("\n请求参数:");
                    requestInfo.getParameters().forEach((key, values) -> {
                        if (values.length == 1) {
                            logMessage.append("\n  ").append(key).append(": ").append(values[0]);
                        } else {
                            logMessage.append("\n  ").append(key).append(": ").append(String.join(", ", values));
                        }
                    });
                }

                // 添加请求体内容
                if (StringUtils.isNotBlank(requestInfo.getRequestBody())) {
                    logMessage.append("\n请求体内容: ").append(requestInfo.getRequestBody());
                }
            }

            // 添加调用栈
            logMessage.append("\n异常调用栈: ").append(stackTrace);
            logMessage.append("\n=== 参数绑定异常监控结束 ===");

            // 一次性打印所有信息
            log.error(logMessage.toString());

        } catch (Exception recordException) {
            // 记录异常监控过程中的异常，但不影响原异常的处理
            log.error("参数绑定异常监控过程中发生异常", recordException);
        }
    }

    /**
     * 获取参数绑定异常的请求信息
     */
    private ParameterBindingRequestInfo getParameterBindingRequestInfo() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attributes = (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                jakarta.servlet.http.HttpServletRequest request = attributes.getRequest();
                ParameterBindingRequestInfo info = new ParameterBindingRequestInfo();

                // 基本请求信息
                info.setUrl(request.getRequestURL().toString());
                info.setMethod(request.getMethod());
                info.setRemoteAddr(request.getRemoteAddr());
                info.setQueryString(request.getQueryString());
                info.setContentType(request.getContentType());
                info.setUserAgent(request.getHeader("User-Agent"));
                info.setReferer(request.getHeader("Referer"));

                // 获取所有请求头
                info.setHeaders(getAllHeaders(request));

                // 获取请求参数
                info.setParameters(getAllParameters(request));

                // 获取请求体
                info.setRequestBody(getRequestBody(request));

                return info;
            }
        } catch (Exception ex) {
            log.debug("获取参数绑定异常请求信息失败", ex);
        }
        return null;
    }

    /**
     * 获取所有请求头
     */
    private Map<String, String> getAllHeaders(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // 对敏感信息进行脱敏
            if ("Authorization".equalsIgnoreCase(headerName) && StringUtils.isNotBlank(headerValue)
                    && headerValue.length() > 10) {
                headerValue = headerValue.substring(0, 10) + "***";
            }
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    /**
     * 获取所有请求参数
     */
    private Map<String, String[]> getAllParameters(jakarta.servlet.http.HttpServletRequest request) {
        return request.getParameterMap();
    }

    /**
     * 获取请求体内容
     */
    private String getRequestBody(jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 如果是ContentCachingRequestWrapper，可以获取缓存的请求体
            if (request instanceof org.springframework.web.util.ContentCachingRequestWrapper) {
                org.springframework.web.util.ContentCachingRequestWrapper wrapper = (org.springframework.web.util.ContentCachingRequestWrapper) request;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    return new String(content, java.nio.charset.StandardCharsets.UTF_8);
                }
            }

            // 对于其他情况，不尝试读取请求体，避免消费流
            return "请求体需要通过RequestBodyCachingFilter缓存才能读取";
        } catch (Exception e) {
            log.debug("获取请求体失败", e);
        }
        return "无请求体或获取失败";
    }

    /**
     * 获取参数绑定异常的调用栈信息
     */
    private String getParameterBindingStackTrace(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            // 找到Spring MVC相关的调用栈
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (className.contains("org.springframework.web") ||
                        className.contains("com.yuntoo.smartcode.controller")) {
                    return String.format("%s.%s(%s:%d)",
                            element.getClassName(), element.getMethodName(),
                            element.getFileName(), element.getLineNumber());
                }
            }
        }
        return "无法获取参数绑定异常调用栈";
    }

    /**
     * 构建错误响应
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("success", false);

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 参数绑定异常请求信息封装类
     */
    private static class ParameterBindingRequestInfo {
        private String url;
        private String method;
        private String remoteAddr;
        private String queryString;
        private String contentType;
        private String userAgent;
        private String referer;
        private Map<String, String> headers;
        private Map<String, String[]> parameters;
        private String requestBody;

        // Getters and Setters
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getRemoteAddr() {
            return remoteAddr;
        }

        public void setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }

        public String getQueryString() {
            return queryString;
        }

        public void setQueryString(String queryString) {
            this.queryString = queryString;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getReferer() {
            return referer;
        }

        public void setReferer(String referer) {
            this.referer = referer;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, String[]> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, String[]> parameters) {
            this.parameters = parameters;
        }

        public String getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(String requestBody) {
            this.requestBody = requestBody;
        }
    }
}
