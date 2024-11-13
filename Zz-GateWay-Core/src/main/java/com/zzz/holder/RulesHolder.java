package com.zzz.holder;

import com.zzz.model.Rule;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class RulesHolder {

    //静态内部类的线程安全由jvm确保
    private static class SingletonHolder {
        private static final RulesHolder INSTANCE = new RulesHolder();
    }


    public static RulesHolder getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     *  规则集合
     */
    static Map<Long, Rule> rules = new ConcurrentHashMap<>();

    /**
     * 服务名称 - 规则
     */
    static Map<String, Rule> nameRules = new ConcurrentHashMap<>();

    /**
     * 路径 - 规则
     */
    static Map<String, Set<Rule>> pathRules = new ConcurrentHashMap<>();

    /**
     * 前缀 - 规则
     */
    static Map<String, Rule> preRules = new ConcurrentHashMap<>();





    public void pullAll(List<Rule> Rules) {
        Rules.forEach(r->{
            rules.put(r.getRuleId(),r);
            nameRules.put(r.getRuleName(),r);
            r.getPath().forEach(v->{
                Set<Rule> set = pathRules.getOrDefault(v,new HashSet<>());

            });
        });
    }

}
