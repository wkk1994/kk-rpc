package com.wkk.learn.kk.rpc.code.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 方法描述
 * @author Wangkunkun
 * @date 2024/3/17 20:15
 */
@Data
public class MethodDesc {

    private Method method;

    private String methodSign;

    private Class returnType;

    public MethodDesc(Method method) {
        this.method = method;
        this.methodSign = MethodUtil.getMethodSign(method);
        this.returnType = method.getReturnType();
    }
}
