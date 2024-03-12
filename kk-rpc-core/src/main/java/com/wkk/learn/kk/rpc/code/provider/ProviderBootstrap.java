package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Wangkunkun
 * @date 2024/3/12 22:45
 */
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Object> skeleton = new HashMap<>();

    /**
     * 构建生产者
     */
    @PostConstruct
    public void buildProviders() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(KkProvider.class);
        beansWithAnnotation.values().forEach(this::getInterfaces);
    }

    public void getInterfaces(Object object) {
        for (Class anInterface : object.getClass().getInterfaces()) {
            skeleton.put(anInterface.getCanonicalName(), object);
        }
    }

    public RpcResponse invokeRequest(RpcRequest request) {
        Object bean = skeleton.get(request.getService());
        Method method = null;
        Object invoke = null;
        try {
            method = getMethodByName(bean.getClass(), request.getMethod());
            invoke = method.invoke(bean, request.getArgs());
            return RpcResponse.success(invoke);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(e.getMessage());
        }
    }

    private Method getMethodByName(Class classz, String methodName) {
        return Arrays.stream(classz.getMethods()).filter(value -> value.getName().equals(methodName)).findAny().get();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
