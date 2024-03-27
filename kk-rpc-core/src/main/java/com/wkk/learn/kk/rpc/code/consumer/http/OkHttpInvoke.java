package com.wkk.learn.kk.rpc.code.consumer.http;

import com.alibaba.fastjson.JSON;
import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;

/**
 * OK http实现消费者执行器
 * @author Wangkunkun
 * @date 2024/3/24 21:30
 */
@Slf4j
public class OkHttpInvoke implements HttpInvoke{

    private OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10)).callTimeout(Duration.ofSeconds(10)).build();

    @Override
    public RpcResponse post(RpcRequest rpcRequest, String url) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.get("application/json"), JSON.toJSONString(rpcRequest))).build();
        ResponseBody body = null;
        try {
            log.info("url : {}", url);
            body = client.newCall(request).execute().body();
            String jsonStr = body.string();
            log.info("result : {}", jsonStr);
            return JSON.parseObject(jsonStr, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
