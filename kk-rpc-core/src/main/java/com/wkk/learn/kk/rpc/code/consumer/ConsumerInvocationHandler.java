package com.wkk.learn.kk.rpc.code.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.api.LoadBalancer;
import com.wkk.learn.kk.rpc.code.api.Router;
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
    private Router router;
    private LoadBalancer loadBalancer;
    private List<String> providers;

    public ConsumerInvocationHandler(Class service, Router router, LoadBalancer loadBalancer, List<String> providers) {
        this.service = service;
        this.router = router;
        this.loadBalancer = loadBalancer;
        this.providers = providers;
    }

    private OkHttpClient client = new Builder().connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10)).callTimeout(Duration.ofSeconds(10)).build();
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> urls = router.choose(providers);
        String chooseUrl = loadBalancer.choose(urls);
        RpcResponse post = post(method, args, chooseUrl);
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

    private RpcResponse post(Method method, Object[] args, String url) throws IOException {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodSign(MethodUtil.getMethodSign(method));
        rpcRequest.setArgs(args);
        rpcRequest.setService(service.getCanonicalName());
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.get("application/json"), JSON.toJSONString(rpcRequest))).build();
        ResponseBody body = client.newCall(request).execute().body();
        String jsonStr = body.string();
        log.info("result : {}", jsonStr);
        return JSON.parseObject(jsonStr, RpcResponse.class);
    }
}
