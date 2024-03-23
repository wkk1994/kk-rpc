package com.wkk.learn.kk.rpc.code.provider;

import com.wkk.learn.kk.rpc.code.RpcRequest;
import com.wkk.learn.kk.rpc.code.RpcResponse;
import com.wkk.learn.kk.rpc.code.annotation.KkProvider;
import com.wkk.learn.kk.rpc.code.api.RegistryCenter;
import com.wkk.learn.kk.rpc.code.meta.MethodDesc;
import com.wkk.learn.kk.rpc.code.meta.ServiceDesc;
import com.wkk.learn.kk.rpc.code.meta.TypeUtil;
import jakarta.annotation.PostConstruct;
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
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, ServiceDesc> skeleton = new HashMap<>();

    private String instance;
    @Value("${server.port}")
    private String port;

    /**
     * 构建生产者
     */
    public void start() {
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

    /**
     * 注册服务
     * @param serviceName
     */
    private void registryService(String serviceName) {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.register(serviceName, this.instance);
    }

    public RpcResponse invokeRequest(RpcRequest request) {
        ServiceDesc serviceDesc = skeleton.get(request.getService());
        if(serviceDesc == null) {
            return RpcResponse.fail("Not Found Service");
        }
        Object invoke = null;
        try {
            MethodDesc methodDesc = serviceDesc.getMethods().get(request.getMethodSign());
            if(methodDesc == null) {
                return RpcResponse.fail("Not Found Method");
            }
            Object[] argTarget = null;
            if(request.getArgs() != null) {
                argTarget = new Object[request.getArgs().length];
                for (int i = 0; i < request.getArgs().length; i++) {
                    argTarget[i] = TypeUtil.cast(request.getArgs()[i], methodDesc.getMethod().getParameterTypes()[i]);
                }
            }
            invoke = methodDesc.getMethod().invoke(serviceDesc.getService(), argTarget);
            return RpcResponse.success(invoke);
        } catch (InvocationTargetException e) {
            return RpcResponse.fail((Exception) e.getTargetException());
        } catch (Exception e) {
            return RpcResponse.fail(e);
        }
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
