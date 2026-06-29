package com.order.service.demo.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class LoggingAspect {

    // ✅ ONLY SERVICE + CONTROLLER
    @Before("execution(* com.order.service.demo.service.*.*(..)) || " +
            "execution(* com.order.service.demo.controller.*.*(..))")
    public void logBefore(JoinPoint jp) {
        System.out.println("➡️ " + jp.getSignature().getName());
    }

    @AfterThrowing(
        pointcut = "execution(* com.order.service.demo.service.*.*(..))",
        throwing = "ex"
    )
    public void logError(JoinPoint jp, Throwable ex) {
        System.out.println("❌ Error: " + ex.getMessage());
    }
}