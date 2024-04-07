package com.fireal.web.util;

import java.lang.reflect.Method;
import java.util.Collection;

import com.fireal.web.core.RequestType;

public class ReflectUtil {

    public static boolean isHandler(Class<?> clazz) {
        //TODO: judge this
        return true;
    }

    public static Collection<Method> getRequestMethods(Class<?> clazz) {
        //TODO: get mapping methods
        return null;
    }

    public static RequestType getRequestType(Method method) {
        //TODO: you know
        return null;
    }

    public static String getMappingPath(Method method) {
        //TODO: emmm
        return null;
    }

}
