package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import com.wkk.learn.kk.rpc.code.meta.InstanceMeta;
import com.wkk.learn.kk.rpc.code.meta.ServiceMeta;
import com.wkk.learn.kk.rpc.code.register.RegistryCenter;
import com.wkk.learn.kk.rpc.code.meta.ServiceDesc;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

    private InstanceMeta instance;
    @Value("${server.port}")
    private Integer port;
    @Value("${app.id}")
    private String appId;
    @Value("${app.namespace}")
    private String appNamespace;
    @Value("${app.env}")
    private String appEnv;

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
        this.instance = new InstanceMeta("http", ip, port, "", null);
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
        ServiceMeta serviceMeta = new ServiceMeta(appId, appNamespace, appEnv, serviceName);
        registryCenter.register(serviceMeta, this.instance);
    }

    /**
     * 取消注册服务
     * @param serviceName
     */
    private void unregistryService(String serviceName) {
        ServiceMeta serviceMeta = new ServiceMeta(appId, appNamespace, appEnv, serviceName);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.unregister(serviceMeta, this.instance);
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
