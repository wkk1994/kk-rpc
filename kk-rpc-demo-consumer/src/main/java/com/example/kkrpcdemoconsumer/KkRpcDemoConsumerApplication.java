package com.example.kkrpcdemoconsumer;

import com.wkk.demo.kk.rpc.api.Order;
import com.wkk.demo.kk.rpc.api.OrderService;
import com.wkk.demo.kk.rpc.api.User;
import com.wkk.demo.kk.rpc.api.UserService;
import com.wkk.learn.kk.rpc.code.annotation.KkConsumer;
import com.wkk.learn.kk.rpc.code.consumer.ConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.List;

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
            User user = userService.findById(101, "1234");
            log.info("findById: {}", user);
            User user1 = null;
            try {
                user1 = userService.findById(404);
            } catch (Exception e) {
                log.error("error" , e);
            }
            log.info("findById: {}", user1);
            User user2 = userService.findById(101L, "longlong");
            log.info("findById: {}", user2);
            Order order = orderService.findById(123);
            log.info("findById: {}", order);
            log.info("findByLong: {}", userService.findByLong());
            log.info("findByLongI: {}", userService.findByLongI());
            log.info("findByInteger: {}", userService.findByInteger());
            log.info("findByListInt: {}", userService.findByListInt());
            log.info("findByIntArr: {}", userService.findByIntArr());
            log.info("findByIntegerArr: {}", userService.findByIntegerArr());
            log.info("findByLongArr: {}", userService.findByLongArr());
            log.info("findByLongArr: {}", userService.findByLongIArr(new int[]{134,12341234,2342}));
//            List<User> userList = userService.findByUserList();
//            log.info("findByLongArr: {}", userList.get(0).getUsername());
        };
    }

}
