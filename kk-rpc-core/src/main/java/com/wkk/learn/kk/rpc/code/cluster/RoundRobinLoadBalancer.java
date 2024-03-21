package com.wkk.learn.kk.rpc.code.cluster;

import com.wkk.learn.kk.rpc.code.api.LoadBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡
 * @author Wangkunkun
 * @date 2024/3/21 21:40
 */
public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if(providers == null || providers.isEmpty()) {
            return null;
        }
        return providers.get((index.getAndIncrement() & 0x7fffffff) % providers.size());
    }
}
