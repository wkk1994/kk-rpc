package com.wkk.learn.kk.rpc.code.consumer.http;

import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;

import java.lang.reflect.Method;

/**
 * 消费者执行器
 * @author Wangkunkun
 * @date 2024/3/24 21:29
 */
public interface HttpInvoke {

    RpcResponse post(RpcRequest rpcRequest, String url);
}
