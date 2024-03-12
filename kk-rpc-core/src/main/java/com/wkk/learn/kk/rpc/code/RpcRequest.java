package com.wkk.learn.kk.rpc.code;

import lombok.Data;

/**
 * RPC请求描述
 * @author Wangkunkun
 * @date 2024/3/12 21:15
 */
@Data
public class RpcRequest {

    /**
     * 服务名（class全限定名）
     */
    private String service;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private Object[] args;
}
