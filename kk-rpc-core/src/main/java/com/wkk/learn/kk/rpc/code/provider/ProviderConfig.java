package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.register.RegistryCenter;
import com.wkk.learn.kk.rpc.code.register.zk.ZkRegistryCenter;
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

    @Bean
    public RegistryCenter registryCenter() {
        return new ZkRegistryCenter();
    }

    @Bean
    public ProviderInvoke providerInvoke(ProviderBootstrap providerBootstrap) {
        return new ProviderInvoke(providerBootstrap);
    }

    @Bean
    public ApplicationRunner getRunner(ProviderBootstrap providerBootstrap) {
        return x -> {
            providerBootstrap.start();
        };
    }
}
