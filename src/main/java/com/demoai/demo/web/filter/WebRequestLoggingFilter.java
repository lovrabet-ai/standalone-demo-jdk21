/*
 * Copyright (c) by lovrabet.com 2025-2035. All right reserved.
 */

package com.demoai.demo.web.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lovrabet.runtime.model.common.GlobalParamsHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.lovrabet.runtime.model.common.GlobalParamsHolder.getInvokeSource;

/**
 * Web请求日志记录过滤器
 * 记录所有HTTP请求的详细信息，包括：
 * - 请求URL、方法、参数
 * - 请求头信息
 * - 请求体内容
 * - 响应状态码、响应体
 * - 请求处理耗时
 * - TraceId追踪
 * - 用户信息
 *
 * @author zzm-躬行
 * @version 1.0.0
 * @date 2026/3/13 15:13
 */
@Slf4j
public class WebRequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 200; // 最大记录长度，避免日志过大
    private static final Set<String> SENSITIVE_HEADERS = new HashSet<>(Arrays.asList(
            "authorization", "cookie", "x-auth-token", "password", "secret"
    ));
    // ObjectMapper默认就是紧凑格式（不换行、不缩进）
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 判断是否需要记录日志
        if (!isLoggable(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 包装请求和响应对象，以便可以多次读取body
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String traceId = getTraceId();

        // 记录请求日志，先合并到Response一起
        // logRequest(wrappedRequest, traceId);

        try {
            // 执行过滤链
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 记录响应日志
            logResponse(wrappedRequest, wrappedResponse, duration, traceId);

            // 必须调用此方法，否则响应内容不会被写回客户端
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * 判断是否需要记录日志
     */
    private boolean isLoggable(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 排除静态资源
        if (uri.matches(".+\\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$")) {
            return false;
        }

        // 排除健康检查和监控端点
        if (uri.startsWith("/actuator") ||
                uri.startsWith("/druid") ||
                uri.equals("/health") ||
                uri.equals("/heartbeat") ||
                uri.equals("/ping")) {
            return false;
        }

        return true;
    }

    /**
     * 记录请求日志
     */
    private void logRequest(ContentCachingRequestWrapper request, String traceId) {
        try {
            StringBuilder logMessage = new StringBuilder();

            // 系统时间
            logMessage.append(getCurrentTimestamp()).append("|");
            // 客户端IP
            logMessage.append(getClientIp(request)).append("|");
            // 请求标识
            logMessage.append("[tid:").append(traceId).append("]|");

            // HTTP方法和URI
            logMessage.append(request.getMethod()).append(" ");
            logMessage.append(request.getRequestURI());

            // 查询参数
            String queryString = request.getQueryString();
            if (queryString != null) {
                logMessage.append("?").append(queryString);
            }
            logMessage.append("|");

            // 请求头（过滤敏感信息）
            // logMessage.append("[Headers:").append(getHeadersAsString(request)).append("]");

            // 请求体
            String payload = getRequestPayload(request, true);
            if (payload != null && !payload.isEmpty()) {
                logMessage.append("|");
                logMessage.append("[body:").append(payload).append("]");
            }

            log.info(logMessage.toString());
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
    }

    /**
     * 记录响应日志
     */
    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                             long duration, String traceId) {
        try {
            StringBuilder logMessage = new StringBuilder();

            // 系统时间
            logMessage.append(getCurrentTimestamp()).append("|");
            // 客户端IP
            logMessage.append(getClientIp(request)).append("|");
            // 请求标识
            logMessage.append("tid:").append(traceId).append("|");
            // 调用来源
            logMessage.append("src:").append(getInvokeSource()).append("|");
            // 用户信息
            String user = getCurrentUser();
            logMessage.append(user).append("|");

            // HTTP方法和URI
            logMessage.append(request.getMethod());
            logMessage.append("|");
            logMessage.append(request.getRequestURI());
            // 查询参数
            String queryString = request.getQueryString();
            if (queryString != null) {
                logMessage.append("?").append(queryString);
            }
            logMessage.append("|");
            // 请求体
            String payload = getRequestPayload(request, true);
            if (payload != null && !payload.isEmpty()) {
                logMessage.append(payload).append("|");
            }

            // 响应状态
            logMessage.append(response.getStatus()).append("|");

            // 耗时
            logMessage.append(duration).append("ms");
            log.info(logMessage.toString());

        } catch (Exception e) {
            log.error("Failed to log response", e);
        }
    }

    /**
     * 获取请求体内容，优化JSON输出
     */
    private String getRequestPayload(ContentCachingRequestWrapper request, boolean isCutOff) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String rawPayload = new String(buf, 0, buf.length, request.getCharacterEncoding());

                // 尝试解析为JSON并优化
                String optimizedPayload = optimizeJsonPayload(rawPayload);

                // 限制长度
                if (isCutOff && optimizedPayload.length() > MAX_PAYLOAD_LENGTH) {
                    return optimizedPayload.substring(0, MAX_PAYLOAD_LENGTH) + "...";
                }
                return optimizedPayload;
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 优化JSON字符串，移除null值和空集合
     * @param jsonString 原始JSON字符串
     * @return 优化后的JSON字符串
     */
    private String optimizeJsonPayload(String jsonString) {
        try {
            JsonNode node = objectMapper.readTree(jsonString);
            JsonNode optimizedNode = removeNullAndEmptyFields(node);
            return objectMapper.writeValueAsString(optimizedNode);
        } catch (Exception e) {
            // 如果不是有效的JSON，直接返回原字符串
            return jsonString;
        }
    }

    /**
     * 递归移除JSON节点中的null值和空集合/数组
     * @param node JSON节点
     * @return 优化后的JSON节点
     */
    private JsonNode removeNullAndEmptyFields(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            ObjectNode result = objectMapper.createObjectNode();

            objectNode.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                JsonNode value = entry.getValue();

                // 跳过null值
                if (value.isNull()) {
                    return;
                }

                // 跳过serialVersionUID
                if ("serialVersionUID".equals(fieldName)) {
                    return;
                }

                // 跳过空数组
                if (value.isArray() && value.size() == 0) {
                    return;
                }

                // 跳过空对象
                if (value.isObject() && value.size() == 0) {
                    return;
                }

                // 递归处理子节点
                JsonNode optimizedValue = removeNullAndEmptyFields(value);
                result.set(fieldName, optimizedValue);
            });

            return result;
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            ArrayNode result = objectMapper.createArrayNode();

            arrayNode.forEach(item -> {
                if (!item.isNull()) {
                    result.add(removeNullAndEmptyFields(item));
                }
            });

            return result;
        }

        return node;
    }

    /**
     * 获取响应体内容
     */
    private String getResponsePayload(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                String payload = new String(buf, 0, Math.min(buf.length, MAX_PAYLOAD_LENGTH),
                        response.getCharacterEncoding());
                if (buf.length > MAX_PAYLOAD_LENGTH) {
                    payload += "...(truncated)";
                }
                return payload;
            } catch (UnsupportedEncodingException e) {
                return "[unknown encoding]";
            }
        }
        return null;
    }

    /**
     * 获取请求头信息（过滤敏感信息）
     */
    private String getHeadersAsString(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            // 过滤敏感头信息
            if (SENSITIVE_HEADERS.contains(headerName.toLowerCase())) {
                headerValue = "***MASKED***";
            }

            headers.put(headerName, headerValue);
        }

        return headers.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For可能包含多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 获取当前用户信息
     */
    private String getCurrentUser() {
        return "";
    }

    /**
     * 获取当前系统时间戳
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    /**
     * 获取 TraceId（优先 OTEL，降级到业务 traceId）
     * <p>
     * 优先级：
     * 1. OTEL traceId（32位十六进制）- 便于与 HyperDX 关联
     * 2. 业务 traceId（GlobalParamsHolder）- 兼容无 OTEL 上下文场景
     * 3. 空字符串
     * </p>
     */
    private String getTraceId() {
        try {
            String bizTraceId = GlobalParamsHolder.getTraceId();
            if (bizTraceId != null && !bizTraceId.isEmpty()) {
                return bizTraceId;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "";
    }
}
