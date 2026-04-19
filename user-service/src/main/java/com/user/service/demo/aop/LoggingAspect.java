package com.user.service.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 🔹 Before method execution
    @Before("execution(* com.user.service.demo.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("➡️ [USER] Entering: " + joinPoint.getSignature().getName());
    }

    // 🔹 After successful execution
    @AfterReturning(
        pointcut = "execution(* com.user.service.demo.service.*.*(..))",
        returning = "result"
    )
    public void logAfter(JoinPoint joinPoint, Object result) {
        System.out.println("✅ [USER] Exiting: " + joinPoint.getSignature().getName());
        System.out.println("📦 [USER] Result: " + result);
    }

    // 🔹 Exception logging
    @AfterThrowing(
        pointcut = "execution(* com.user.service.demo.service.*.*(..))",
        throwing = "ex"
    )
    public void logException(JoinPoint joinPoint, Throwable ex) {
        System.out.println("❌ [USER] Exception in: " + joinPoint.getSignature().getName());
        System.out.println("⚠️ [USER] Error: " + ex.getMessage());
    }
}