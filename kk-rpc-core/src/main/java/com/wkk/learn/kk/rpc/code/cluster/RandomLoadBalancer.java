package com.wkk.learn.kk.rpc.code.cluster;

import com.wkk.learn.kk.rpc.code.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡
 * @author Wangkunkun
 * @date 2024/3/21 21:38
 */
public class RandomLoadBalancer implements LoadBalancer {

    private Random random = new Random();

    @Override
    public String choose(List<String> providers) {
        if(providers == null || providers.isEmpty()) {
            return null;
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
