package com.example.kkrpcdemoconsumer.config;

import com.wkk.learn.kk.rpc.code.api.LoadBalancer;
import com.wkk.learn.kk.rpc.code.api.RegistryCenter;
import com.wkk.learn.kk.rpc.code.api.Router;
import com.wkk.learn.kk.rpc.code.api.ZkRegistryCenter;
import com.wkk.learn.kk.rpc.code.cluster.RandomLoadBalancer;
import com.wkk.learn.kk.rpc.code.cluster.RoundRobinLoadBalancer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消费者配置
 * @author Wangkunkun
 * @date 2024/3/21 21:48
 */
@Data
@Component
@Configuration
public class ConsumerConfiguration {

    @Value("#{${provider.urls:\"http://127.0.0.1:8081,http://127.0.0.1:8082\"}}")
    private List<String> providers;

    @Bean
    public Router<String> router() {
        return Router.DEFAULT;
    }

    @Bean
    public LoadBalancer<String> loadBalancer() {
        return new RoundRobinLoadBalancer<String>();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
        return new ZkRegistryCenter();
    }
}
