package com.fly.dynamic.controller;

import com.fly.dynamic.dto.Page;
import com.fly.dynamic.dto.RuleBriefDto;
import com.fly.dynamic.dto.RuleDetailDto;
import com.fly.dynamic.entity.Rule;
import com.fly.dynamic.entity.RuleResult;
import com.fly.dynamic.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.fly.dynamic.common.RuleErrorMessage.*;

/**
 * 规则controller
 *
 * @author guoxiang
 */
@RestController
@RequestMapping("rule")
@Slf4j
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    /**
     * 新增或修改规则
     * @param rule  规则
     * @return      result
     */
    @PostMapping
    public RuleResult saveRule(@RequestBody Rule rule) {
        log.info("save rule: {}", rule);

        String name = rule.getName();
        Assert.hasText(name, NAME_ERROR);
        Assert.isTrue(name.matches("^[0-9a-zA-Z_]{4,10}$"), NAME_ERROR);

        checkRuleText(rule.getRuleText());
        ruleService.save(rule);

        log.info("save rule success id: {}", rule.getId());
        return RuleResult.success(rule.getId());
    }


    /**
     * 校验规则是否有问题
     *
     * @param ruleText 规则文本
     */
    private void checkRuleText(String ruleText) {
        Assert.hasText(ruleText, RULE_TEXT_NULL_ERROR);

        KieHelper helper = new KieHelper();
        Results results = helper.addContent(ruleText, ResourceType.DRL).verify();
        List<Message> messages = results.getMessages();
        if (messages.isEmpty()) {
            return;
        }

        log.error("verify result: {}", results);
        String error = RULE_SYNTAX_ERROR + "\n" +
                messages.stream().map(Message::getText).collect(Collectors.joining("\n"));

        throw new IllegalArgumentException(error);
    }


    /**
     * 删除规则
     *
     * @param id id
     * @return  规则
     */
    @DeleteMapping
    public RuleResult deleteById(@RequestParam Long id) {
        log.info("delete rule by id: {}", id);
        Rule delete = ruleService.delete(id);

        log.info("delete rule finish: {}", delete);
        return RuleResult.success();
    }


    /**
     * 分页查询
     * @param pageNo    pageNo
     * @param pageSize  pageSize
     * @return          page
     */
    @GetMapping
    public RuleResult page(@RequestParam Integer pageNo,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) String name) {

        Page<RuleBriefDto> page = ruleService.page(pageNo, pageSize, name);
        return RuleResult.success(page);
    }


    @GetMapping("{id}")
    public RuleResult getById(@PathVariable Long id) {
        Rule rule = ruleService.getById(id);
        return RuleResult.success(RuleDetailDto.convertFrom(rule));
    }

}
