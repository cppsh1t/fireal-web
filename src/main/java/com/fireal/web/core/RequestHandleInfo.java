package com.fireal.web.core;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
public class RequestHandleInfo implements Comparable<RequestHandleInfo>{

    private MethodHandle methodHandle;
    private RequestType requestType;
    private String mappingPath;
    private int order;
    private Collection<RequestParam> requestParams = new ArrayList<>();

    public RequestHandleInfo(MethodHandle methodHandle, RequestType requestType, String mappingPath, int order) {
        this.methodHandle = methodHandle;
        this.requestType = requestType;
        this.mappingPath = mappingPath;
        this.order = order;
    }

    //TODO:如果验证正确，可以返回转化后的参数,应该加一个序列化接口
    public RequestParamHolder validate(String url) {
        
        return null;
    }

    public Object handle(RequestParamHolder params) {
        // try {
        //     return methodHandle.invoke(arguments);
        // } catch (Throwable e) {
        //     e.printStackTrace();
        //     return null;
        // }
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

    //TODO: 计划不用他
    @Deprecated
    public boolean hasPathVariable() {
        return false;
    }

    @Override
    public int compareTo(RequestHandleInfo o) {
        return this.order - o.order;
    }

    public void addReuqestParam(RequestParam... params) {
        requestParams.addAll(Arrays.asList(params));
    }

    public void addReuqestParam(RequestParam param) {
        requestParams.add(param);
    }

}
