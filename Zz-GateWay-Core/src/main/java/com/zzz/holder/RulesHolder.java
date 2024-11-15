package com.zzz.holder;

import com.zzz.model.Rule;

import java.util.*;
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
    static Map<String, Set<Rule>> preRules = new ConcurrentHashMap<>();


    public void pullAll(List<Rule> Rules) {
        Rules.forEach(r->{
            rules.put(r.getRuleId(),r);
            nameRules.put(r.getRuleName(),r);
            r.getPath().forEach(v->{
                Set<Rule> set = pathRules.getOrDefault(v,new HashSet<>());
                set.add(r);
                pathRules.put(v,set);
            });
            r.getPrefix().forEach(p->{
                Set<Rule> set = pathRules.getOrDefault(p,new HashSet<>());
                set.add(r);
                preRules.put(p,set);
            });
        });
    }

    public Rule getRuleById(long id) {
        return rules.get(id);
    }

    public Rule getRuleByName(String name) {
        return nameRules.get(name);
    }

    public Set<Rule> getRulesByPath(String path) {
        return pathRules.get(path);
    }

    public Set<Rule> getPreRulesByPath(String path) {
        return preRules.get(path);
    }

    public void deleteRuleById(long id) {
        rules.remove(id);
    }

    public void deleteRuleByName(String name) {
        nameRules.remove(name);
    }

    public void deletePreRulesByPath(String path) {
        preRules.remove(path);
    }

    public void deletePreRulesByName(String name) {
        preRules.remove(name);
    }

    public void clear(Map<Object,Object> map){
        map.clear();
    }

    public void deleteAll() {
        rules.clear();
        nameRules.clear();
        pathRules.clear();
        preRules.clear();
    }

}
