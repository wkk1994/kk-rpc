package com.wkk.learn.kk.rpc.code.consumer;

import com.wkk.learn.kk.rpc.code.annotation.KkConsumer;
import com.wkk.learn.kk.rpc.code.api.Filter;
import com.wkk.learn.kk.rpc.code.api.LoadBalancer;
import com.wkk.learn.kk.rpc.code.api.Router;
import com.wkk.learn.kk.rpc.code.api.RpcContext;
import com.wkk.learn.kk.rpc.code.meta.MethodUtil;
import com.wkk.learn.kk.rpc.code.meta.InstanceMeta;
import com.wkk.learn.kk.rpc.code.meta.ServiceMeta;
import com.wkk.learn.kk.rpc.code.register.RegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消费者启动类
 * 代理加了@KkConsumer的属性
 * @author Wangkunkun
 * @date 2024/3/12 22:45
 */
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    @Value("${app.id}")
    private String appId;
    @Value("${app.namespace}")
    private String appNamespace;
    @Value("${app.env}")
    private String appEnv;

    public void start() {
        Map<String, Filter> filterMap = applicationContext.getBeansOfType(Filter.class);
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);

        RpcContext rpcContext = new RpcContext(new ArrayList<>(filterMap.values()), router, loadBalancer);

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames).forEach(beanDefinitionName -> {
            Object bean = applicationContext.getBean(beanDefinitionName);
            List<Field> fieldList = MethodUtil.findAnnotationField(bean.getClass(), KkConsumer.class);
            if(CollectionUtils.isEmpty(fieldList)) {
                return;
            }
            for (Field field : fieldList) {
                Object fieldInstance = createConsumerFromRegistry(field, rpcContext, registryCenter);
                field.setAccessible(true);
                try {
                    field.set(bean, fieldInstance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 生产代理类
     * @param field
     * @param rpcContext
     * @param registryCenter
     * @return
     */
    private Object createConsumerFromRegistry(Field field, RpcContext rpcContext, RegistryCenter registryCenter) {
        Class<?> service = field.getType();
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = new ServiceMeta(appId, appNamespace, appEnv, serviceName);
        List<InstanceMeta> instanceMetas = registryCenter.fetchAll(serviceMeta);
        List<String> providers = instanceMetas.stream().map(InstanceMeta::toUrl).collect(Collectors.toList());
        registryCenter.subscribe(serviceMeta, (changeEvent) -> {
            providers.clear();
            providers.addAll(changeEvent.getNodes().stream().map(InstanceMeta::toUrl).toList());
        });
        Object consumer = stub.get(serviceName);
        if(consumer == null) {
            consumer = createConsumer(service, rpcContext, providers);
            stub.put(serviceName, consumer);
        }
        return consumer;
    }

    private Object createConsumer(Class<?> service, RpcContext rpcContext, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new ConsumerInvocationHandler(service, rpcContext, providers));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
