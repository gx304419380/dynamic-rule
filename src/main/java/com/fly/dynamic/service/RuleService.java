package com.fly.dynamic.service;

import com.fly.dynamic.dao.RuleDao;
import com.fly.dynamic.dto.Page;
import com.fly.dynamic.dto.RuleBriefDto;
import com.fly.dynamic.entity.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.fly.dynamic.common.RuleErrorMessage.*;
import static org.kie.api.io.ResourceType.DRL;

/**
 * 规则引擎动态获取session工具类
 *
 * @author guoxiang
 * @since 2021-07
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService {

    private static final Map<Long, KieContainer> CACHE_ID = new HashMap<>();
    private static final Map<String, Long> CACHE_NAME = new HashMap<>();

    private final ApplicationContext context;
    private final RuleDao ruleDao;
    private final JdbcTemplate jdbcTemplate;


    /**
     * 初始化数据库
     */
    @EventListener(classes = ApplicationReadyEvent.class)
    public void init() throws IOException {
        String sql = "create table if not exists tb_rule\n" +
                "(\n" +
                "id bigint auto_increment\n" +
                "primary key,\n" +
                "name varchar(32) null,\n" +
                "rule_text text not null,\n" +
                "create_time datetime null,\n" +
                "update_time datetime null,\n" +
                "description varchar(200) null\n" +
                ")";

        try {
            log.info("建表语句：\n{}", sql);
            jdbcTemplate.execute(sql);
            log.info("建表完成");
        } catch (Exception e) {
            log.error("不支持当前数据库，请手动建表：", e);
        }

    }


    /**
     * 新增或保存
     *
     * @param rule 规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Rule rule) {
        Long id = rule.getId();
        LocalDateTime now = LocalDateTime.now();

        Boolean exist = ruleDao.exist(rule.getName(), id);
        Assert.isTrue(!exist, NAME_EXIST_ERROR);

        //如果是新增
        if (ObjectUtils.isEmpty(id)) {
            rule.setCreateTime(now);
        }

        rule.setUpdateTime(now);
        String ruleText = rule.getRuleText();

        Assert.hasText(ruleText, RULE_TEXT_NULL_ERROR);

        //保存数据库
        ruleDao.save(rule);

        //刷新缓存
        uninstall(rule);
    }


    /**
     * 分页查询
     * @param pageNo    页码
     * @param pageSize  大小
     * @param name      name
     * @return          page
     */
    public Page<RuleBriefDto> page(Integer pageNo, Integer pageSize, String name) {
        return ruleDao.findByNameLike(name, pageNo, pageSize);
    }


    /**
     * 查询详情
     *
     * @param id id
     * @return  详情
     */
    public Rule getById(Long id) {

        Optional<Rule> rule = ruleDao.findById(id);

        return rule.orElse(null);
    }


    /**
     * 删除规则
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public Rule delete(Long id) {

        Optional<Rule> rule = ruleDao.findById(id);

        if (!rule.isPresent()) {
            return null;
        }

        ruleDao.deleteById(id);

        //清缓存
        uninstall(rule.get());
        return rule.get();
    }

    
    /**
     * 根据id获取对应的session
     *
     * @param id    id
     * @return      session
     */
    public KieSession getSessionById(Long id) {
        KieContainer container = getContainerById(id);

        KieSession session = container.newKieSession();

        //设置日志和spring容器
        session.insert(log);
        session.insert(context);
        session.insert(jdbcTemplate);

        return session;
    }


    /**
     * 根据名称获取规则容器session
     *
     * @param name 名称
     * @return      session
     */
    public KieSession getSessionByName(String name) {
        Long id = CACHE_NAME.get(name);

        if (id != null) {
            return getSessionById(id);
        }

        synchronized (CACHE_NAME) {
            CACHE_NAME.computeIfAbsent(name, this::getIdByName);
            id = CACHE_NAME.get(name);
            return getSessionById(id);
        }
    }

    /**
     * 根据名称获取规则id
     *
     * @param name  name
     * @return      id
     */
    private Long getIdByName(String name) {
        Rule rule = ruleDao.findByName(name);
        Assert.notNull(rule, CANNOT_FIND_RULE_ERROR);
        return rule.getId();
    }


    /**
     * 卸载某规则
     *
     * @param rule rule
     */
    private void uninstall(Rule rule) {
        Long id = rule.getId();

        //卸载container
        KieContainer container = CACHE_ID.get(id);
        if (container != null) {
            CACHE_ID.remove(id);
            container.dispose();
        }

        //清理name
        CACHE_NAME.remove(rule.getName());
    }


    /**
     * 根据id获取container
     *
     * @param id    id
     * @return      容器
     */
    private KieContainer getContainerById(Long id) {
        KieContainer container = CACHE_ID.get(id);

        if (container != null) {
            return container;
        }

        synchronized (CACHE_ID) {
            String rule = getRuleById(id);
            return CACHE_ID.computeIfAbsent(id, k -> new KieHelper().addContent(rule, DRL).getKieContainer());
        }
    }


    /**
     * 从数据库获取规则
     *
     * @param id    id
     * @return      规则
     */
    private String getRuleById(Long id) {
        Optional<Rule> rule = ruleDao.findById(id);

        Assert.isTrue(rule.isPresent(), RULE_NULL_ERROR);
        return rule.get().getRuleText();
    }


}
