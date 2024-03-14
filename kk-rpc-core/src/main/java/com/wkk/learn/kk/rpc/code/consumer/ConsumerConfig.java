package com.wkk.learn.kk.rpc.code.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 消费者配置
 * @author Wangkunkun
 * @date 2024/3/12 22:46
 */
@Slf4j
@Configuration
public class ConsumerConfig {

    @Bean
    public ConsumerBootstrap providerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    ApplicationRunner consumerStart(ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerStart 。。。。");
            consumerBootstrap.start();
            log.info("consumerStared 。。。。");
        };
    }
}
