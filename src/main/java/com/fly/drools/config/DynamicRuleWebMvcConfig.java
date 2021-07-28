package com.fly.drools.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author guoxiang
 * @version 1.0.0
 * @since 2021/3/3
 */
@Configuration
public class DynamicRuleWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/dynamic-rule/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/dynamic-rule/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/dynamic-rule/")
                .setViewName("forward:/dynamic-rule/index.html");

        registry.addViewController("/dynamic-rule")
                .setViewName("redirect:/dynamic-rule/index.html");
    }
}
