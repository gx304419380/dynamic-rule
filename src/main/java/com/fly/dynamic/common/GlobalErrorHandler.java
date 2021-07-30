package com.fly.dynamic.common;

import com.fly.dynamic.entity.RuleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author guoxiang
 * @version 1.0.0
 * @since 2021/7/26
 */
@RestControllerAdvice(basePackages = "com.fly.dynamic.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(Exception.class)
    public RuleResult handleException(Exception e) {
        log.error("- rule exception", e);
        return RuleResult.fail(e.getMessage());
    }

}
