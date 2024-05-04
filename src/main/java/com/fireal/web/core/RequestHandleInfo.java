package com.fireal.web.core;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.fireal.web.exception.RequestParamInfoException;
import com.fireal.web.util.StringUtils;
import fireal.structure.Tuple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RequestHandleInfo implements Comparable<RequestHandleInfo>{

    private final Method method;
    private final RequestType requestType;
    private final String mappingPath;
    private final int order;
    private final Collection<RequestParamInfo> requestParams = new ArrayList<>();
    private final Collection<String> requestMappingLimit = new ArrayList<>();
    private final Object invoker;

    public RequestHandleInfo(Method method, Object invoker, RequestType requestType, String mappingPath, int order) {
        this.method = method;
        this.requestType = requestType;
        this.mappingPath = mappingPath;
        this.order = order;
        this.invoker = invoker;
    }

    //TODO:这个url应该是验证PathVariable
    //TODO: 以后再做空优化
    public RequestParamHolder validate(String url, HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> queryMap = StringUtils.parseQueryString(url);
        boolean containAll = requestMappingLimit.stream().allMatch(queryMap::containsKey);
        if (!containAll) return null;
        RequestParamHolder requestParamHolder = new RequestParamHolder();
        for(RequestParamInfo requestParamInfo : requestParams) {
            Class<?> originType = requestParamInfo.getOriginType();
            if (originType != null) {
                if (originType == HttpServletRequest.class) {
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, req));
                    continue;
                }
                if (originType == HttpServletResponse.class) {
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, resp));
                    continue;
                }
                if (originType == HttpSession.class) {
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, req.getSession()));
                    continue;
                }
                throw new RequestParamInfoException(originType);
            } else {
                String targetString = queryMap.get(requestParamInfo.getName());
                if (targetString == null) {
                    //TODO: write this exception type
                    if (requestParamInfo.isRequired()) return null;
                    Object targetObj = requestParamInfo.getDefaultValue();
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, targetObj));
                } else {
                    Class<?> targetType = requestParamInfo.getParamType();
                    Object targetObj;
                    if (targetType.isPrimitive()) {
                        String targetStr = queryMap.get(requestParamInfo.getName());
                        targetObj = WebInitializer.stringToObject(targetStr, targetType);
                    } else {
                        targetObj = WebInitializer.constructObject(queryMap, targetType);
                    }
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, targetObj));
                }
            }
        }
        return requestParamHolder;
    }

    public Object handle(RequestParamHolder holder) {
         try {
             Object[] arguments = holder.contents.stream().map(Tuple::getSecondKey).toArray();
             return method.invoke(invoker, arguments);
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

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(RequestHandleInfo o) {
        return this.order - o.order;
    }

    public void addRequestParam(RequestParamInfo... params) {
        requestParams.addAll(Arrays.asList(params));
    }

    public void addRequestParam(Collection<RequestParamInfo> params) {
        requestParams.addAll(params);
    }

    public void addRequestParam(RequestParamInfo param) {
        requestParams.add(param);
    }

    public void addRequestMappingLimit(String limit) {
        requestMappingLimit.add(limit);
    }

    public void addRequestMappingLimit(String... limit) {
        requestMappingLimit.addAll(Arrays.asList(limit));
    }
}
