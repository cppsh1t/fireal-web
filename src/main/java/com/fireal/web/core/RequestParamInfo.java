package com.fireal.web.core;

import com.fireal.web.data.ConstructorInfo;

public class RequestParamInfo {

    private Class<?> paramAnnoClass;
    private String name;
    private Object defaultValue;
    private boolean required;
    private Class<?> paramType;
    private Class<?> originType;
    private ConstructorInfo constructorInfo;

    private RequestParamInfo() { }

    public static RequestParamInfo simple(Class<?> paramAnnoClass, String name, Object defaultValue, boolean required, Class<?> paramType) {
        RequestParamInfo requestParamInfo = new RequestParamInfo();
        requestParamInfo.paramAnnoClass = paramAnnoClass;
        requestParamInfo.name = name;
        requestParamInfo.defaultValue = defaultValue;
        requestParamInfo.required = required;
        requestParamInfo.paramType = paramType;
        return requestParamInfo;
    }

    public static RequestParamInfo origin(Class<?> clazz) {
        RequestParamInfo requestParamInfo = new RequestParamInfo();
        requestParamInfo.originType = clazz;
        return requestParamInfo;
    }

    public static RequestParamInfo complex(Class<?> clazz) {
        RequestParamInfo requestParamInfo = new RequestParamInfo();
        requestParamInfo.constructorInfo = ConstructorInfo.make(clazz);
        return requestParamInfo;
    }

    public Class<?> getParamAnnoClass() {
        return paramAnnoClass;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public Class<?> getOriginType() {
        return originType;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public ConstructorInfo getConstructorInfo() {
        return constructorInfo;
    }
}
