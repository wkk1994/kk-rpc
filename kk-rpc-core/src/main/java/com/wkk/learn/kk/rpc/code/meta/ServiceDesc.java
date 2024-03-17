package com.wkk.learn.kk.rpc.code.meta;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务描述
 * @author Wangkunkun
 * @date 2024/3/17 20:31
 */
@Data
public class ServiceDesc {

    private Object service;
    private String serviceName;
    private Class interfaceClass;

    private Map<String, MethodDesc> methods = new HashMap<>();

    public ServiceDesc(Object service, Class interfaceClass) {
        this.service = service;
        this.serviceName = interfaceClass.getCanonicalName();
        this.interfaceClass = interfaceClass;
        initMethod();
    }

    private void initMethod() {
        if(this.interfaceClass == null) {
            return;
        }
        Method[] methodArr = this.interfaceClass.getMethods();
        for (Method method : methodArr) {
            if(MethodUtil.isLocalMethod(method)) {
                continue;
            }
            MethodDesc methodDesc = new MethodDesc(method);
            methods.put(methodDesc.getMethodSign(), methodDesc);
        }
    }


}
