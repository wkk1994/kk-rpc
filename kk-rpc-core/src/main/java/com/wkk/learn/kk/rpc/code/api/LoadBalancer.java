package com.wkk.learn.kk.rpc.code.api;

import java.util.List;

/**
 * 负载均衡接口
 * @author Wangkunkun
 * @date 2024/3/21 21:34
 */
public interface LoadBalancer {

    String choose(List<String> providers);

    LoadBalancer DEFAULT = (providers) -> {
        if (providers == null || providers.isEmpty()) {
            return null;
        }else {
            return providers.get(0);
        }
    };
}
