package org.market.bingebuddies.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(org.market.bingebuddies.controllers..*)")
    public void controllerMethods() {}

    @Pointcut("within(org.market.bingebuddies.services..*)")
    public void serviceMethods() {}

    @Before("controllerMethods()")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        log.info(">>>> Entering controller method: {}.{}() with args: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    @After("controllerMethods()")
    public void logAfterControllerMethod(JoinPoint joinPoint) {
        log.info("<<<< Controller method finished: {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
        );
    }

    @Before("serviceMethods()")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        log.info(">>>> Entering service method: {}.{}() with args: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturningServiceMethod(JoinPoint joinPoint, Object result) {
        log.info("<<<< Exiting service method: {}.{}() with result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result
        );
    }
}
