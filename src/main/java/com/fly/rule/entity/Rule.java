package com.fly.rule.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author guoxiang
 */
@Data
public class Rule {

    private Long id;

    private String name;

    private String description;

    private String ruleText;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
