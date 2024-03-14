package com.example.kkrpcdemoconsumer;

import com.wkk.demo.kk.rpc.api.UserService;
import com.wkk.learn.kk.rpc.code.annotation.KkConsumer;
import com.wkk.learn.kk.rpc.code.consumer.ConsumerConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(ConsumerConfig.class)
@SpringBootApplication
@Component
public class KkRpcDemoConsumerApplication {

    @KkConsumer
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(KkRpcDemoConsumerApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return x -> {
            userService.findById(11);
        };
    }
}
