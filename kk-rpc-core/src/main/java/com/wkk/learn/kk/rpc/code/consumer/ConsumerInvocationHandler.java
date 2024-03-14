package com.wkk.learn.kk.rpc.code.consumer;

import com.alibaba.fastjson.JSON;
import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;

import static okhttp3.OkHttpClient.*;

/**
 * 消费者执行器
 * @author Wangkunkun
 * @date 2024/3/13 22:39
 */
@Data
public class ConsumerInvocationHandler implements InvocationHandler {

    private Class service;

    public ConsumerInvocationHandler(Class service) {
        this.service = service;
    }

    private OkHttpClient client = new Builder().connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10)).callTimeout(Duration.ofSeconds(10)).build();
    @Override
    public RpcResponse invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethod(method.getName());
        rpcRequest.setArgs(args);
        rpcRequest.setService(service.getCanonicalName());
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8080/")
                .post(RequestBody.create(MediaType.get("application/json"), JSON.toJSONString(rpcRequest))).build();
        String body = client.newCall(request).execute().body().toString();
        return JSON.parseObject(body, RpcResponse.class);

    }
}
