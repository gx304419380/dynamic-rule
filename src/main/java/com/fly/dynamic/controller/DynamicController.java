package com.fly.dynamic.controller;

import com.fly.dynamic.entity.RuleResult;
import com.fly.dynamic.service.DynamicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 动态controller
 *
 * @author guoxiang
 */
@RestController
@RequestMapping("/rule/dynamic")
@Slf4j
@RequiredArgsConstructor
public class DynamicController {
    private final DynamicService dynamicService;

    /**
     * 动态接口 POST
     *
     * @param ruleId    ruleId
     * @param param param
     * @return      result
     */
    @PostMapping("/{ruleId}")
    public RuleResult handlePost(@PathVariable Long ruleId, @RequestBody Map<String, Object> param) {

        log.debug("- handle post dynamic rule: {}, param: {}", ruleId, param);

        RuleResult result = dynamicService.handleById(ruleId, param);

        log.debug("- dynamic post controller result: {}", result);
        return result;
    }

    /**
     * 动态接口 GET
     *
     * @param ruleId    ruleId
     * @param param param
     * @return      result
     */
    @GetMapping("/{ruleId}")
    public RuleResult handleGet(@PathVariable Long ruleId, @RequestParam Map<String, Object> param) {

        log.debug("- handle get dynamic rule: {}, param: {}", ruleId, param);

        RuleResult result = dynamicService.handleById(ruleId, param);

        log.debug("- dynamic get controller result: {}", result);
        return result;
    }

    /**
     * 动态接口 POST
     *
     * @param name  rule name
     * @param param param
     * @return      result
     */
    @PostMapping("/name/{name}")
    public RuleResult handlePost(@PathVariable String name, @RequestBody Map<String, Object> param) {

        log.debug("- handle post dynamic rule: {}, param: {}", name, param);

        RuleResult result = dynamicService.handleByName(name, param);

        log.debug("- dynamic post controller result: {}", result);
        return result;
    }

    /**
     * 动态接口 GET
     *
     * @param name    ruleId
     * @param param param
     * @return      result
     */
    @GetMapping("/name/{name}")
    public RuleResult handleGet(@PathVariable String name, @RequestParam Map<String, Object> param) {

        log.debug("- handle get dynamic rule: {}, param: {}", name, param);

        RuleResult result = dynamicService.handleByName(name, param);

        log.debug("- dynamic get controller result: {}", result);
        return result;
    }


}
