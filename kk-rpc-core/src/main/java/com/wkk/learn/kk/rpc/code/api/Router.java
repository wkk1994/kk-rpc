package com.wkk.learn.kk.rpc.code.api;

import java.util.List;

/**
 * 路由
 * @author Wangkunkun
 * @date 2024/3/21 21:33
 */
public interface Router {

    List<String> choose(List<String> providers);

    Router DEFAULT = providers -> providers;
}
