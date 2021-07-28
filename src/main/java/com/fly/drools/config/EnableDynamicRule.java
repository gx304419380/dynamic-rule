package com.fly.drools.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author guoxiang
 * @version 1.0.0
 * @since 2021/7/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicRuleConfig.class)
public @interface EnableDynamicRule {
}
