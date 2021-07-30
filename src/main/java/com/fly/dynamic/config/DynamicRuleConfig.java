package com.fly.dynamic.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author guoxiang
 * @version 1.0.0
 * @since 2021/7/28
 */
@Configuration
@ConditionalOnProperty(value = "dynamic.rule.enable", matchIfMissing = true)
@EnableAsync(proxyTargetClass = true)
@ComponentScan("com.fly.dynamic")
public class DynamicRuleConfig {
}


