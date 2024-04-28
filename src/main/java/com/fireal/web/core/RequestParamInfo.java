package com.fireal.web.core;

public class RequestParamInfo {

    private Class<?> paramAnnoClass;
    private String name;
    private Object defaultValue;
    private boolean required;
    private Class<?> originType;

    public RequestParamInfo(Class<?> paramAnnoClass, String name, Object defaultValue, boolean required) {
        this.paramAnnoClass = paramAnnoClass;
        this.name = name;
        this.defaultValue = defaultValue;
        this.required = required;
    }

    public RequestParamInfo(Class<?> originType) {
        this.originType = originType;
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

}
