package com.example.kkrpcdemoconsumer;

import com.wkk.demo.kk.rpc.api.Order;
import com.wkk.demo.kk.rpc.api.OrderService;
import com.wkk.demo.kk.rpc.api.User;
import com.wkk.demo.kk.rpc.api.UserService;
import com.wkk.learn.kk.rpc.code.annotation.KkConsumer;
import com.wkk.learn.kk.rpc.code.consumer.ConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(ConsumerConfig.class)
@SpringBootApplication
@Component
@Slf4j
public class KkRpcDemoConsumerApplication {

    @KkConsumer
    private UserService userService;
    @KkConsumer
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(KkRpcDemoConsumerApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return x -> {
            User user = userService.findById(101);
            log.info("findById: {}", user);
            Order order = orderService.findById(123);
            log.info("findById: {}", order);
        };
    }
}
