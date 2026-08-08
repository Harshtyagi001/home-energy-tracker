package com.energytracker.user_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("execution(* com.energytracker.user_service.controller..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.nanoTime();

        Object result = joinPoint.proceed();

        long endTime = System.nanoTime();

        long executionTimeMs = (endTime - startTime) / 1_000_000;

        log.info(
                "Controller method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                executionTimeMs
        );

        return result;
    }
}