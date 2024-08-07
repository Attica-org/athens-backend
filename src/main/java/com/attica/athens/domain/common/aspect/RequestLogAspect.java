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
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            logger.error(ex.getMessage());
            throw ex;
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            String declaringType = pjp.getSignature().getDeclaringTypeName();
            String callMethod = pjp.getSignature().getName();
            Object[] requestArgs = pjp.getArgs();

            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String method = request.getMethod();
                    String requestUrl = request.getRequestURL().toString();
                    String clientIp = request.getHeader("X-Forwarded-For");

                    logger.info("HTTP Request: {} {} from IP: {}, Method: {}.{}, Args: {}, ExecutionTime: {} ms",
                            method,
                            requestUrl,
                            clientIp,
                            declaringType,
                            callMethod,
                            requestArgs,
                            executionTime
                    );
                } else {
                    logger.info("Non-HTTP Request: Method: {}.{}, Args: {}, ExecutionTime: {} ms",
                            declaringType,
                            callMethod,
                            requestArgs,
                            executionTime
                    );
                }
            } catch (IllegalStateException e) {
                logger.info("WebSocket/Other Request: Method: {}.{}, Args: {}, ExecutionTime: {} ms",
                        declaringType,
                        callMethod,
                        requestArgs,
                        executionTime
                );
            }
        }

        return result;
    }
}
