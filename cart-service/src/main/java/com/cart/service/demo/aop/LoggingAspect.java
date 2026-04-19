package com.cart.service.demo.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 🔹 Before execution
    @Before("execution(* com.cart.service.demo.service.*.*(..))")
    public void before(JoinPoint jp) {
        System.out.println("➡️ [CART] Entering: " + jp.getSignature().getName());
    }

    // 🔹 After success
    @AfterReturning(
            pointcut = "execution(* com.cart.service.demo.service.*.*(..))",
            returning = "result"
    )
    public void after(JoinPoint jp, Object result) {
        System.out.println("✅ [CART] Exiting: " + jp.getSignature().getName());
        System.out.println("📦 [CART] Result: " + result);
    }

    // 🔹 Exception logging
    @AfterThrowing(
            pointcut = "execution(* com.cart.service.demo.service.*.*(..))",
            throwing = "ex"
    )
    public void exception(JoinPoint jp, Throwable ex) {
        System.out.println("❌ [CART] Exception in: " + jp.getSignature().getName());
        System.out.println("⚠️ [CART] Error: " + ex.getMessage());
    }
    
    @Before("execution(* com.cart.service.demo.controller.*.*(..))")
    public void controllerLog(JoinPoint jp) {
        System.out.println("🌐 [API] Called: " + jp.getSignature().getName());
    }
}