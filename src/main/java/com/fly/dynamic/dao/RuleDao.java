package com.fly.dynamic.dao;

import com.fly.dynamic.dto.Page;
import com.fly.dynamic.dto.RuleBriefDto;
import com.fly.dynamic.entity.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.*;

import static com.fly.dynamic.common.RuleErrorMessage.ID_NULL_ERROR;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author guoxiang
 */
@Repository
@RequiredArgsConstructor
public class RuleDao {

    private final NamedParameterJdbcTemplate namedJdbc;

    private final JdbcTemplate jdbc;

    private static final BeanPropertyRowMapper<RuleBriefDto> DTO_MAPPER = new BeanPropertyRowMapper<>(RuleBriefDto.class);
    private static final BeanPropertyRowMapper<Rule> RULE_MAPPER = new BeanPropertyRowMapper<>(Rule.class);

    /**
     * 根据名称查询
     *
     * @param name      name
     * @param pageNo    pageNo
     * @param pageSize  pageSize
     * @return          page
     */
    public Page<RuleBriefDto> findByNameLike(String name, Integer pageNo, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (pageNo - 1) * pageSize);
        params.put("pageSize", pageSize);

        String countSql = "select count(*) from tb_rule ";
        String sql = "select id, name, description, create_time from tb_rule ";

        if (hasText(name)) {
            params.put("name", name);
            sql += "where name like CONCAT('%', :name, '%') ";
            countSql += "where name like CONCAT('%', :name, '%') ";
        }

        sql += "limit :offset, :pageSize";

        Integer count = namedJdbc.queryForObject(countSql, params, Integer.class);
        if (Objects.equals(count, 0)) {
            return Page.empty();
        }

        List<RuleBriefDto> list = namedJdbc.query(sql, params, DTO_MAPPER);
        return new Page<>(count, list);
    }

    /**
     * 新增或者修改
     *
     * @param rule  规则
     * @return      id
     */
    public Long save(Rule rule) {

        String update = "update tb_rule set name=:name, " +
                "rule_text=:ruleText, " +
                "update_time=:updateTime, " +
                "description=:description " +
                "where id=:id";

        if (rule.getId() != null) {
            namedJdbc.update(update, new BeanPropertySqlParameterSource(rule));
            return rule.getId();
        }

        String sql = "insert into tb_rule (id, name, rule_text, create_time, update_time, description) " +
                "VALUES (:id, :name, :ruleText, :createTime, :updateTime, :description)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, new BeanPropertySqlParameterSource(rule), keyHolder);

        Number key = keyHolder.getKey();
        Assert.notNull(key, ID_NULL_ERROR);
        rule.setId(key.longValue());
        return rule.getId();
    }

    /**
     * 根据id查询
     *
     * @param id    id
     * @return      规则
     */
    public Optional<Rule> findById(Long id) {
        String sql = "select * from tb_rule where id=?";
        List<Rule> list = jdbc.query(sql, RULE_MAPPER, id);
        return list.stream().findAny();
    }

    /**
     * 删除规则
     *
     * @param id    id
     */
    public void deleteById(Long id) {
        jdbc.update("delete from tb_rule where id=?", id);
    }

    /**
     * 判断名称是否存在
     *
     * @param name  name
     * @param id    id
     * @return      count
     */
    public Boolean exist(String name, Long id) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("id", id);

        String sql = "select count(*) from tb_rule where name=:name ";
        if (id != null) {
            sql += "and id != :id ";
        }

        sql += "limit 1";

        Integer count = namedJdbc.queryForObject(sql, param, Integer.class);
        return !Objects.equals(count, 0);
    }

    /**
     * 根据名称查找rule
     *
     * @param name  name
     * @return      rule
     */
    public Rule findByName(String name) {
        List<Rule> list = jdbc.query("select * from tb_rule where name=?", RULE_MAPPER, name);
        return list.stream().findAny().orElse(null);
    }
}
