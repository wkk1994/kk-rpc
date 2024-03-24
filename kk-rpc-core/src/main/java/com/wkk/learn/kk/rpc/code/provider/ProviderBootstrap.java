package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import com.wkk.learn.kk.rpc.code.register.RegistryCenter;
import com.wkk.learn.kk.rpc.code.meta.MethodDesc;
import com.wkk.learn.kk.rpc.code.meta.ServiceDesc;
import com.wkk.learn.kk.rpc.code.meta.TypeUtil;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Wangkunkun
 * @date 2024/3/12 22:45
 */
@Slf4j
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RegistryCenter registryCenter;

    private Map<String, ServiceDesc> skeleton = new HashMap<>();

    private String instance;
    @Value("${server.port}")
    private String port;

    /**
     * 构建生产者
     */
    public void start() {
        registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.start();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(KkProvider.class);
        beansWithAnnotation.values().forEach(this::getInterfaces);
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.instance = ip + "_" + port;
        skeleton.keySet().forEach(this::registryService);
    }

    public void getInterfaces(Object object) {
        for (Class anInterface : object.getClass().getInterfaces()) {
            ServiceDesc serviceDesc = new ServiceDesc(object, anInterface);
            skeleton.put(serviceDesc.getServiceName(), serviceDesc);
        }
    }

    @PreDestroy
    public void stop() {
        log.info("provider stop...");
        this.skeleton.keySet().forEach(this::unregistryService);
        registryCenter.stop();
    }

    /**
     * 注册服务
     * @param serviceName
     */
    private void registryService(String serviceName) {
        registryCenter.register(serviceName, this.instance);
    }

    /**
     * 取消注册服务
     * @param serviceName
     */
    private void unregistryService(String serviceName) {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.unregister(serviceName, this.instance);
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
