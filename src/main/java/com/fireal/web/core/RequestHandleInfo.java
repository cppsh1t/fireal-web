package com.fireal.web.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.fireal.web.exception.ParamterCastException;
import com.fireal.web.exception.RequestParamInfoException;
import com.fireal.web.util.StringUtils;
import com.fireal.web.util.TypeUtil;

import ch.qos.logback.classic.Logger;
import fireal.structure.Tuple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RequestHandleInfo implements Comparable<RequestHandleInfo> {

    private static final Logger log = (Logger) LoggerFactory.getLogger(RequestHandleInfo.class);

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

    public RequestParamHolder validate(String url, HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Validating request parameters for URL: {}", url);
        Map<String, String> queryMap = StringUtils.parseQueryString(url);
        boolean containAll = requestMappingLimit.stream().allMatch(queryMap::containsKey);
        if (!containAll) {
            log.debug("Request mapping limit not satisfied for URL: {}", url);
            return null;
        }
        RequestParamHolder requestParamHolder = new RequestParamHolder();
        for (RequestParamInfo requestParamInfo : requestParams) {
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
                if (requestParamInfo.getConstructorInfo() != null) {
                    Object targetObj = requestParamInfo.getConstructorInfo().build(queryMap);
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, targetObj));
                } else if (targetString == null) {
                    if (requestParamInfo.isRequired())
                        return null;
                    Object targetObj = requestParamInfo.getDefaultValue();
                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, targetObj));
                } else {
                    Class<?> targetType = requestParamInfo.getParamType();
                    Object targetObj;
                    if (!TypeUtil.canCast(targetType))
                        throw new ParamterCastException(targetString, targetType);
                    targetObj = WebInitializer.stringToObject(targetString, targetType);
                    if (targetObj == null)
                        return null;

                    requestParamHolder.contents.add(new Tuple<>(requestParamInfo, targetObj));
                }
            }
        }
        log.debug("Validation of request parameters successful for URL: {}", url);
        return requestParamHolder;
    }

    public Object handle(RequestParamHolder holder) {
        try {
            Object[] arguments = holder.contents.stream().map(Tuple::getSecondKey).toArray();
            Object result = method.invoke(invoker, arguments);
            log.debug("Method invocation successful, result: {}", result);
            return result;
        } catch (Throwable e) {
            log.error("Error handling request", e);
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

    @Override
    public String toString() {
        return "RequestHandleInfo{" +
                "requestType=" + requestType +
                ", mappingPath='" + mappingPath + '\'' +
                '}';
    }
}
