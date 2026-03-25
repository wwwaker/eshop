package com.example.eshop.aspect;

import com.example.eshop.entity.SysLog;
import com.example.eshop.entity.User;
import com.example.eshop.service.SysLogService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AOP日志切面：自动记录Controller/Service操作日志
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    @Autowired
    private SysLogService sysLogService;

    // 拦截所有Controller
    @Pointcut("execution(* com.example.eshop.controller..*.*(..))" +
            "&& !execution(* com.example.eshop.service.SysLogService..*(..))")
    public void logPointcut() {}

    // 环绕通知：记录正常操作日志
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long time = System.currentTimeMillis() - start;

        // 记录INFO级别日志
        saveLog(joinPoint, "INFO", "执行成功，耗时: " + time + "ms");
        return result;
    }

    // 异常通知：记录错误日志
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        saveLog(joinPoint, "ERROR", "执行异常: " + e.getMessage());
        log.error("方法执行异常", e);
    }

    // 保存日志到数据库
    private void saveLog(JoinPoint joinPoint, String level, String content) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();

            // 获取请求信息
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String requestUrl = request.getRequestURI();
            String ip = getClientIp(request);

            // 获取当前用户
            User user = AuthUtil.getCurrentUser(request.getSession());
            String username = user != null ? user.getUsername() : "未登录";

            // 构建日志
            SysLog sysLog = new SysLog();
            sysLog.setLogLevel(level);
            sysLog.setLogContent(content);
            sysLog.setCreateTime(LocalDateTime.now());
            sysLog.setClassName(className);
            sysLog.setMethodName(methodName);
            sysLog.setRequestUrl(requestUrl);
            sysLog.setUsername(username);
            sysLog.setIp(ip);

            sysLogService.saveLog(sysLog);
        } catch (Exception e) {
            log.error("记录系统日志失败", e);
        }
    }

    // 获取客户端IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}