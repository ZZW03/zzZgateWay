package com.zzz.filter.loadbalance.impl;

import com.zzz.filter.loadbalance.LoadBalanceGatewayRule;
import com.zzz.holder.ServiceHolder;
import com.zzz.model.GatewayContext;
import com.zzz.model.ServiceInstance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashRobin implements LoadBalanceGatewayRule {

    private TreeMap<Long, ServiceInstance> nodeMap = new TreeMap<>();

    private static  class singleton{
        private static final ConsistentHashRobin instance = new ConsistentHashRobin();
    }

    public static ConsistentHashRobin getInstance(){
        return singleton.instance;
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx) {
        List<ServiceInstance> serviceInstances= ServiceHolder.getInstance().getServiceInstancesByServiceName(ctx.getRule().getServerName());
        pullAll(serviceInstances);
        return selectNode(ctx.getRequest().getFinalUrl());
    }

    private long md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            // 取前 8 个字节作为哈希值
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash <<= 8;
                hash |= (digest[i] & 0xFF);
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public void pullAll( List<ServiceInstance> list) {
        list.forEach(v->{
            long l = md5Hash(v.getIp() + ":" + v.getPort());
            nodeMap.put(l,v);
        });
    }

    public ServiceInstance selectNode(String id) {
        long hash = md5Hash(id);
        // 使用 tailMap 找到大于等于 hash 的第一个节点
        SortedMap<Long, ServiceInstance> tailMap = nodeMap.tailMap(hash);
        if (tailMap.isEmpty()) {
            // 如果没有找到，返回第一个节点
            return nodeMap.firstEntry().getValue();
        } else {
            // 返回找到的第一个节点
            return tailMap.get(tailMap.firstKey());
        }
    }
}
