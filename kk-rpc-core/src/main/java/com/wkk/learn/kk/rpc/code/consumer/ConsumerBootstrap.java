package com.wkk.learn.kk.rpc.code.consumer;

import com.wkk.learn.kk.rpc.code.annotation.KkConsumer;
import com.wkk.learn.kk.rpc.code.api.Filter;
import com.wkk.learn.kk.rpc.code.api.LoadBalancer;
import com.wkk.learn.kk.rpc.code.api.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
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
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    @Autowired
    private Router router;
    private Environment environment;
    @Autowired
    private LoadBalancer loadBalancer;
    private ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames).forEach(beanDefinitionName -> {
            Object bean = applicationContext.getBean(beanDefinitionName);
            List<Field> fieldList = findConsumerField(bean.getClass());
            if(CollectionUtils.isEmpty(fieldList)) {
                return;
            }
            for (Field field : fieldList) {
                Object fieldInstance = getConsumer(field);
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
     * @return
     */
    private Object getConsumer(Field field) {
        Class<?> service = field.getType();
        String serviceName = service.getCanonicalName();
        Object consumer = stub.get(serviceName);
        if(consumer == null) {
            consumer = createConsumer(service);
            stub.put(serviceName, consumer);
        }
        return consumer;
    }

    private Object createConsumer(Class<?> service) {
        String urls = environment.getProperty("provider.urls");
        assert urls != null;
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new ConsumerInvocationHandler(service, router, loadBalancer, List.of(urls.split(","))));
    }

    /**
     * 查找有@KkConsumber注解的属性
     * @param classz
     * @return
     */
    private List<Field> findConsumerField(Class classz) {
        List<Field> fieldList = new LinkedList<>();
        while (classz != null) {
            Field[] fields = classz.getDeclaredFields();
            fieldList.addAll(Arrays.stream(fields).filter(field -> field.getAnnotation(KkConsumer.class) != null).collect(Collectors.toList()));
            classz = classz.getSuperclass();
        }
        return fieldList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
