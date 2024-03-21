package com.wkk.learn.kk.rpc.code.api;

import lombok.Data;

import java.util.List;

/**
 * @author Wangkunkun
 * @date 2024/3/21 22:53
 */
@Data
public class RpcContext {

    private List<Filter> filters;

    private Router router;

    private LoadBalancer loadBalancer;

    public RpcContext(List<Filter> filters, Router router, LoadBalancer loadBalancer) {
        this.filters = filters;
        this.router = router;
        this.loadBalancer = loadBalancer;
    }
}
