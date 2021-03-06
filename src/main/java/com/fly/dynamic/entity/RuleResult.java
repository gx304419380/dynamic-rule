package com.fly.dynamic.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author guoxiang
 */
@Data
@Accessors(chain = true)
public class RuleResult {

    private String message;

    private Integer code;

    private Object data;

    public static RuleResult success() {
        return new RuleResult().setCode(0);
    }

    public static RuleResult success(Object data) {
        return new RuleResult().setCode(0).setData(data);
    }

    public static RuleResult fail(String message) {
        return new RuleResult().setCode(1).setMessage(message);
    }
}
