package com.fireal.web.core;

import java.lang.invoke.MethodHandle;

public class RequestHandleInfo {

    private MethodHandle methodHandle;
    private RequestType requestType;
    private String mappingPath;

    public RequestHandleInfo(MethodHandle methodHandle, RequestType requestType, String mappingPath) {
        this.methodHandle = methodHandle;
        this.requestType = requestType;
        this.mappingPath = mappingPath;
    }

    public boolean validate(Request request) {
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

    public RequestType getRequestType() {
        return requestType;
    }

    public String getMappingPath() {
        return mappingPath;
    }

}
