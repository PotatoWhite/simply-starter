package io.easywalk.simply.controllable;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class SimplyLoggingAspect {


    @Before(value = "@annotation(SimplyLogging)")
    private void loggingRequest(JoinPoint jp) {
        HttpServletRequest request     = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Object             requestBody = null;

        MethodSignature methodSignature  = (MethodSignature) jp.getSignature();
        Annotation[][]  annotationMatrix = methodSignature.getMethod().getParameterAnnotations();
        int             index            = -1;
        for (Annotation[] annotations : annotationMatrix) {
            index++;
            for (Annotation annotation : annotations) {
                if (!(annotation instanceof RequestBody))
                    continue;
                requestBody = jp.getArgs()[index];
            }
        }

        log.info("[REQ] {} {} Param:{}, RequestBody:{}", request.getMethod(), request.getRequestURI(), request.getQueryString(), requestBody);
    }

    @AfterReturning(value = "@annotation(SimplyLogging)", returning = "ret")
    private void loggingResponse(Object ret) throws RuntimeException {
        HttpServletRequest  request  = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        log.info("[RES] {} {} Param:{}, Response:{} {}", request.getMethod(), request.getRequestURI(), request.getQueryString(), response.getStatus(), Optional.ofNullable(ret).orElse("Response body is empty"));
    }

    @AfterThrowing(value = "@annotation(SimplyLogging)", throwing = "exception")
    private void loggingError(Exception exception) throws RuntimeException {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.debug("[ERR] {} {} Param:{}, trace:{}", request.getMethod(), request.getRequestURI(), request.getQueryString(), writer.toString());
    }
}
