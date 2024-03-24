package com.wkk.learn.kk.rpc.code.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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


    /**
     * 查找有指定注解的属性
     * @param classz
     * @return
     */
    public static List<Field> findAnnotationField(Class classz, Class<? extends Annotation> annotation) {
        List<Field> fieldList = new LinkedList<>();
        while (classz != null) {
            Field[] fields = classz.getDeclaredFields();
            fieldList.addAll(Arrays.stream(fields).filter(field -> field.getAnnotation(annotation) != null).collect(Collectors.toList()));
            classz = classz.getSuperclass();
        }
        return fieldList;
    }
}
