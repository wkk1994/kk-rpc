package com.wkk.learn.kk.rpc.code.meta;

import java.lang.reflect.Method;

/**
 * 方法工具类
 * @author Wangkunkun
 * @date 2024/3/17 20:17
 */
public class MethodUtil {

    /**
     * 获取方法签名
     * @param method
     * @return
     */
    public static String getMethodSign(Method method) {
        String methodSign = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            methodSign += ("@" + parameterType.getCanonicalName());
        }
        return methodSign;
    }

    public static boolean isLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }
}
