package com.fly.dynamic.common;

import lombok.experimental.UtilityClass;

/**
 * @author guoxiang
 */
@UtilityClass
public class RuleErrorMessage {
    public static final String RULE_NULL_ERROR = "规则不存在！";
    public static final String RULE_TEXT_NULL_ERROR = "规则文本为空！";
    public static final String RULE_SYNTAX_ERROR = "规则语法错误！";
    public static final String ID_NULL_ERROR = "规则id为空错误！";
    public static final String NAME_ERROR = "规则名称错误：4-10位数字字母下划线";
    public static final String NAME_EXIST_ERROR = "规则名称已存在！";
    public static final String CANNOT_FIND_RULE_ERROR = "规则不存在！";
}
