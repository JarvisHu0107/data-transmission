package com.jarvis.dts.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: workspace
 * @description:使用list按位置填充对象顺序由注解BeanOrder标识
 * @author: gfx
 * @create: 2019-04-24 11:57
 **/
public class Reflect {

    /**
     * 按beanorder顺序获取方法名
     *
     * @param claxx
     * @param methodHeadName
     * @param <T>
     * @return
     */
    public static <T> List<Method> getDeclaredMethodByName(Class<T> claxx, MethodHeadName methodHeadName, Function<List<Method>, List<Method>> function) {
        String name = methodHeadName.name;
        List<Method> methodList;
        Map<String, Method> methodMap;
        methodMap = Arrays.stream(claxx.getMethods())
                .filter(method -> method.getName().contains(name))
                .collect(Collectors.toMap(Method::getName, method -> method, (k1, k2) -> k2));

        methodList = Arrays.stream(claxx.getDeclaredFields())
                .map(field -> {
                    String fieldName = field.getName();
                    String methodName;
                    methodName = name + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    return methodMap.get(methodName);
                }).filter(Objects::nonNull).collect(Collectors.toList());
        methodList = methodList.size() == 0 ? Arrays.stream(claxx.getMethods()).filter(method -> method.getName().contains(name)).collect(Collectors.toList()) : methodList;
        if (function != null) {
            methodList = function.apply(methodList);
        }

        return methodList;
    }

    public static <T> List<Method> getMethodByName(Class<T> claxx, MethodHeadName methodHeadName, Function<List<Method>, List<Method>> function) {
        String name = methodHeadName.name;
        List<Method> methodList;
        Map<String, Method> methodMap;
        methodMap = Arrays.stream(claxx.getMethods())
                .filter(method -> method.getName().contains(name))
                .collect(Collectors.toMap(Method::getName, method -> method, (k1, k2) -> k2));

        methodList = methodMap.values().stream().collect(Collectors.toList());
        methodList = methodList.size() == 0 ? Arrays.stream(claxx.getMethods()).filter(method -> method.getName().contains(name)).collect(Collectors.toList()) : methodList;
        if (function != null) {
            methodList = function.apply(methodList);
        }

        return methodList;
    }


    public enum MethodHeadName {
        GET("get"), SET("set");

        public String name;

        MethodHeadName(String name) {
            this.name = name;
        }
    }



}
