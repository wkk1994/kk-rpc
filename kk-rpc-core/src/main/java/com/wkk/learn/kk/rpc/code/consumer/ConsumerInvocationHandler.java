package com.wkk.learn.kk.rpc.code.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.api.LoadBalancer;
import com.wkk.learn.kk.rpc.code.api.Router;
import com.wkk.learn.kk.rpc.code.api.RpcContext;
import com.wkk.learn.kk.rpc.code.consumer.http.HttpInvoke;
import com.wkk.learn.kk.rpc.code.consumer.http.OkHttpInvoke;
import com.wkk.learn.kk.rpc.code.meta.MethodUtil;
import com.wkk.learn.kk.rpc.code.meta.TypeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

import static okhttp3.OkHttpClient.*;

/**
 * 消费者执行器
 * @author Wangkunkun
 * @date 2024/3/13 22:39
 */
@Data
@Slf4j
public class ConsumerInvocationHandler implements InvocationHandler {

    private Class service;
    private RpcContext rpcContext;
    private List<String> providers;
    private HttpInvoke httpInvoke = new OkHttpInvoke();

    public ConsumerInvocationHandler(Class<?> service, RpcContext rpcContext, List<String> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> urls = rpcContext.getRouter().choose(providers);
        Object chooseUrl = rpcContext.getLoadBalancer().choose(urls);
        RpcResponse post = post(method, args, chooseUrl.toString());
        if(post.isSuccess()) {
            Object data = post.getData();
            try {
                if(data instanceof JSONObject) {
                    return ((JSONObject) data).toJavaObject(method.getReturnType());
                }else {
                    return TypeUtil.cast(data, method.getReturnType());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.error("invoke error", post.getException());
        throw post.getException();
    }

    private RpcResponse post(Method method, Object[] args, String url) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodSign(MethodUtil.getMethodSign(method));
        rpcRequest.setArgs(args);
        rpcRequest.setService(service.getCanonicalName());
        return httpInvoke.post(rpcRequest, url);
    }
}
