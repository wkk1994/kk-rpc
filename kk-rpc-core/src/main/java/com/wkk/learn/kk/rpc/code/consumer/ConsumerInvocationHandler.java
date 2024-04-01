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
import com.wkk.learn.kk.rpc.code.ex.RpcException;
import com.wkk.learn.kk.rpc.code.governance.SlidingTimeWindow;
import com.wkk.learn.kk.rpc.code.meta.InstanceMeta;
import com.wkk.learn.kk.rpc.code.meta.MethodUtil;
import com.wkk.learn.kk.rpc.code.meta.TypeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    List<String> isolatedProvides = new LinkedList<>();
    List<String> halfOpenProvides = new LinkedList<>();
    private Map<String, SlidingTimeWindow> windowMap = new HashMap<>();
    private static final int windowSize = 30;
    private static final int errorMaxNUm = 5;

    private ScheduledExecutorService executorService;

    public ConsumerInvocationHandler(Class<?> service, RpcContext rpcContext, List<String> providers) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.providers = providers;
        this.executorService = Executors.newScheduledThreadPool(1);
        this.executorService.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug("====halfOpen");
        this.halfOpenProvides.clear();
        this.halfOpenProvides.addAll(this.isolatedProvides);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> urls = rpcContext.getRouter().choose(providers);
        String chooseUrl = null;
        if(halfOpenProvides.isEmpty()) {
            chooseUrl = (String) rpcContext.getLoadBalancer().choose(urls);
        }else {
            chooseUrl = halfOpenProvides.remove(0);
        }

        RpcResponse response = null;
        try {
            response = post(method, args, chooseUrl);
        } catch (Exception e) {
            // 记录异常，如果超过次数，节点放入隔离列表
            SlidingTimeWindow window = windowMap.computeIfAbsent(chooseUrl, k -> new SlidingTimeWindow(windowSize));
            window.record(System.currentTimeMillis());
            log.debug("instance: {} in window with {}", chooseUrl, window.getSum());
            if(window.getSum() > errorMaxNUm) {
                isolate(chooseUrl);
            }
            log.error("invoke error", e);
            throw new RpcException(e, RpcException.INVOKE_ERROR);
        }
        // TODO 并发
        if(!this.providers.contains(chooseUrl)) {
            isolatedProvides.remove(chooseUrl);
            providers.add(chooseUrl);
            log.debug("instance {} is recovered, isolatedProvides = {} providers = {}", chooseUrl, isolatedProvides, providers);
        }
        if(response.isSuccess()) {
            Object data = response.getData();
            try {
                if(data instanceof JSONObject) {
                    return ((JSONObject) data).toJavaObject(method.getReturnType());
                }else {
                    return TypeUtil.cast(data, method.getReturnType());
                }
            } catch (Exception e) {
                throw new RpcException(e, RpcException.TYPE_CAST_ERROR);
            }
        }else {
            log.error("invoke error", response.getException());
            throw response.getException();
        }
    }

    private void isolate(String chooseUrl) {
        log.warn("isolate : {}", chooseUrl);
        this.providers.remove(chooseUrl);
        this.isolatedProvides.add(chooseUrl);
    }

    private RpcResponse post(Method method, Object[] args, String url) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodSign(MethodUtil.getMethodSign(method));
        rpcRequest.setArgs(args);
        rpcRequest.setService(service.getCanonicalName());
        return httpInvoke.post(rpcRequest, url);
    }
}
