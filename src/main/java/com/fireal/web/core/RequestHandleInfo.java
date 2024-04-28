package com.fireal.web.core;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public class RequestHandleInfo implements Comparable<RequestHandleInfo>{

    private MethodHandle methodHandle;
    private RequestType requestType;
    private String mappingPath;
    private int order;
    private Collection<RequestParamInfo> requestParams = new ArrayList<>();
    private Collection<String> requestMappingLimit = new ArrayList<>();

    public RequestHandleInfo(MethodHandle methodHandle, RequestType requestType, String mappingPath, int order) {
        this.methodHandle = methodHandle;
        this.requestType = requestType;
        this.mappingPath = mappingPath;
        this.order = order;
    }

    //TODO:如果验证正确，可以返回转化后的参数,应该加一个序列化接口
    public RequestParamHolder validate(String url, HttpServletRequest req, HttpServletResponse resp) {
        //TODO: 不仅有方法内参数的验证，还要有method自己映射注解的参数要求
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

    public void addReuqestParam(RequestParamInfo... params) {
        requestParams.addAll(Arrays.asList(params));
    }

    public void addReuqestParam(RequestParamInfo param) {
        requestParams.add(param);
    }

}
