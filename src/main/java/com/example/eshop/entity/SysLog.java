package com.example.eshop.entity;

import java.time.LocalDateTime;

/**
 * 系统日志实体
 */
public class SysLog {
    private Long id;
    // 日志级别 ERROR/WARN/INFO/DEBUG/TRACE
    private String logLevel;
    // 日志内容
    private String logContent;
    // 操作时间
    private LocalDateTime createTime;
    // 操作类名
    private String className;
    // 操作方法
    private String methodName;
    // 请求URL
    private String requestUrl;
    // 操作用户名
    private String username;
    // 请求IP
    private String ip;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}