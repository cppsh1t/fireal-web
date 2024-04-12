package com.fireal.web.core;

import java.lang.invoke.MethodHandle;
import java.util.Map;

public class RequestHandleInfo implements Comparable<RequestHandleInfo>{

    private MethodHandle methodHandle;
    private RequestType requestType;
    private String mappingPath;
    private int order;

    public RequestHandleInfo(MethodHandle methodHandle, RequestType requestType, String mappingPath, int order) {
        this.methodHandle = methodHandle;
        this.requestType = requestType;
        this.mappingPath = mappingPath;
        this.order = order;
    }

    public boolean validate(String url) {
        //TODO: 
        return false;
    }

    public Object handle(Object[] arguments) {
        try {
            return methodHandle.invoke(arguments);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object handle(Object[] arguments, Map<String,Object> pathVaiableMap) {
        //TODO:
        return null;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getMappingPath() {
        return mappingPath;
    }

    public int getOrder() {
        return order;
    }

    public boolean hasPathVariable() {
        //TODO:
        return false;
    }

    @Override
    public int compareTo(RequestHandleInfo o) {
        return this.order - o.order;
    }

}
