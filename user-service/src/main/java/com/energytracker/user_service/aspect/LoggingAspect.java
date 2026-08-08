package com.energytracker.user_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.energytracker.user_service.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info(
                "Entering method: {}",
                joinPoint.getSignature().getName()
        );
    }

    @AfterReturning(
            pointcut = "execution(* com.energytracker.user_service.service..*(..))",
            returning = "result"
    )
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info(
                "Exiting method: {} with result: {}",
                joinPoint.getSignature().getName(),
                result
        );
    }
}