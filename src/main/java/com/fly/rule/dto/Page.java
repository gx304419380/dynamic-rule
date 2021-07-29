package com.fly.rule.dto;

import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author guoxiang
 * @version 1.0.0
 * @since 2021/7/29
 */
@Data
public class Page<T> {
    private Integer totalElements;
    private List<T> content;

    public Page() {
    }

    public Page(Integer totalElements, List<T> content) {
        this.totalElements = totalElements;
        this.content = content;
    }

    public static <T> Page<T> empty() {
        return new Page<>(0, emptyList());
    }
}
