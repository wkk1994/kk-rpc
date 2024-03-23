package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.api.RegistryCenter;
import com.wkk.learn.kk.rpc.code.api.ZkRegistryCenter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Wangkunkun
 * @date 2024/3/12 22:46
 */
@Configuration
public class ProviderConfig {

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
        return new ZkRegistryCenter();
    }

    @Bean
    public ApplicationRunner getRunner(ProviderBootstrap providerBootstrap) {
        return x -> {
            providerBootstrap.start();
        };
    }
}
