package com.attica.athens.domain.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RequestLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogAspect.class);

    @Around("bean(*Controller)")
    public Object aroundLogging(final ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            logger.error(ex.getMessage());
            throw ex;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            String method = request.getMethod();
            String requestUrl = request.getRequestURL().toString();
            String clientIp = request.getHeader("X-Forwarded-For");
            String declaringType = pjp.getSignature().getDeclaringTypeName();
            String callMethod = pjp.getSignature().getName();
            Object[] requestArgs = pjp.getArgs();

            logger.info("Request: {} {} from IP: {}, Method: {}.{}, Args: {}, ExecutionTime: {} ms",
                    method,
                    requestUrl,
                    clientIp,
                    declaringType,
                    callMethod,
                    requestArgs,
                    executionTime
            );
        }

        return result;
    }
}
