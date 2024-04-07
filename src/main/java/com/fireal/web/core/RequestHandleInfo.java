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

    public void handle(String url) {
        //TODO: invoke the methodHandle
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getMappingPath() {
        return mappingPath;
    }

}
