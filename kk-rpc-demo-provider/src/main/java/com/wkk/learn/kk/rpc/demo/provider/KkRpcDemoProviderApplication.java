package com.wkk.learn.kk.rpc.demo.provider;

import com.wkk.demo.kk.rpc.api.User;
import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import com.wkk.learn.kk.rpc.code.provider.ProviderBootstrap;
import com.wkk.learn.kk.rpc.code.provider.ProviderConfig;
import com.wkk.learn.kk.rpc.code.provider.ProviderInvoke;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
@SpringBootApplication
@Import(ProviderConfig.class)
public class KkRpcDemoProviderApplication {

    @Autowired
    private ProviderInvoke providerInvoke;

    @Autowired
    private UserServiceImpl userService;

    public static void main(String[] args) {
        SpringApplication.run(KkRpcDemoProviderApplication.class, args);
    }




    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerInvoke.invoke(request);
    }

    @PostMapping("/set-time-out")
    public User setTimeOut(@RequestParam(name = "timeOut") int timeOut) {
        return userService.setTimeOut(timeOut);
    }


    /*@Bean
    ApplicationRunner getRunner() {
        RpcRequest request = new RpcRequest();
        request.setArgs(new Object[]{100});
        request.setService("com.wkk.demo.kk.rpc.api.UserService");
        request.setMethod("findById");
        return x -> {
            RpcResponse rpcResponse = this.invoke(request);
            System.out.println("return : " + rpcResponse.getData());
        };
    }*/
}
