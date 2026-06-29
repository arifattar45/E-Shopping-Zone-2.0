package com.product.service.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 🔹 Before method execution
    @Before("execution(* com.product.service.demo.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("➡️ Entering: " + joinPoint.getSignature().getName());
    }

    // 🔹 After successful execution
    @AfterReturning(pointcut = "execution(* com.product.service.demo.service.*.*(..))",
                    returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        System.out.println("✅ Exiting: " + joinPoint.getSignature().getName());
        System.out.println("📦 Result: " + result);
    }

    // 🔹 Exception logging
    @AfterThrowing(pointcut = "execution(* com.product.service.demo.service.*.*(..))",
                   throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        System.out.println("❌ Exception in: " + joinPoint.getSignature().getName());
        System.out.println("⚠️ Error: " + ex.getMessage());
    }
}
