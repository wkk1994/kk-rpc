package com.example.kk.rpc.demo.consumer;

import com.example.kk.rpc.demo.zk.ZookeeperServer;
import com.example.kkrpcdemoconsumer.KkRpcDemoConsumerApplication;
import com.wkk.learn.kk.rpc.demo.provider.KkRpcDemoProviderApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = KkRpcDemoConsumerApplication.class)
class KkRpcDemoConsumerApplicationTests {

    static ApplicationContext applicationContext;
    static ZookeeperServer zookeeperServer = new ZookeeperServer();

    @SneakyThrows
    @BeforeAll
    static void init() {
        zookeeperServer.start();
        applicationContext = SpringApplication.run(KkRpcDemoProviderApplication.class, "--server.port=8094");
    }
    @Test
    void contextLoads() {
        System.out.println("contextLoads....");
    }


    @AfterAll
    static void destroy() {
        SpringApplication.exit(applicationContext, () -> 1);
        zookeeperServer.stop();
    }
}
