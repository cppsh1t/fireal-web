package com.fireal.web.core;

public class RequestParam {

    private Class<?> paramAnnoClass;
    private String name;
    private Object defaultValue;
    private boolean required;

    public RequestParam(Class<?> paramAnnoClass, String name, Object defaultValue, boolean required) {
        this.paramAnnoClass = paramAnnoClass;
        this.name = name;
        this.defaultValue = defaultValue;
        this.required = required;
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

    

}
