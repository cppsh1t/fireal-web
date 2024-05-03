package com.fireal.web.core;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fireal.web.anno.Order;
import com.fireal.web.path.AntPathMatcher;
import com.fireal.web.path.PathMatcher;
import com.fireal.web.util.ReflectUtil;

import fireal.core.Container;
import fireal.definition.BeanDefinition;
import fireal.util.BeanDefinitionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {

    private Container container;
    private List<RequestHandleInfo> requestHandleInfos = new ArrayList<>();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private RequestParamBuilder requestParamBuilder = new RequestParamBuilder();

    public DispatcherServlet(Container container) {
        this.container = container;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Collection<BeanDefinition> beanDefinitions = BeanDefinitionUtil.getBeanDefinitions(container);
        Lookup lookup = MethodHandles.lookup();

        for (var def : beanDefinitions) {
            if (!ReflectUtil.isHandler(def.getObjectType()))
                continue;
            Collection<Method> methods = ReflectUtil.getRequestMethods(def.getObjectType());
            if (methods == null || methods.size() == 0)
                continue;

            for (Method method : methods) {
                RequestType requestType = ReflectUtil.getRequestType(method);
                //TODO: 这个path好像应该再加上父级的
                String mappingPath = ReflectUtil.getMappingPath(method);
                try {
                    MethodHandle methodHandle = lookup.unreflect(method);
                    methodHandle = methodHandle.bindTo(container.getBean(def.getKeyType()));
                    int order = 0;
                    if (method.isAnnotationPresent(Order.class)) {
                        order = method.getAnnotation(Order.class).value();
                    }
                    RequestHandleInfo requestHandleInfo = new RequestHandleInfo(methodHandle, requestType, mappingPath,
                            order);
                    Collection<RequestParamInfo> params = requestParamBuilder.build(method);
                    String[] limits = ReflectUtil.getMappingLimit(method);
                    requestHandleInfo.addRequestMappingLimit(limits);
                    if (params != null) requestHandleInfo.addRequestParam(params);
                    requestHandleInfos.add(requestHandleInfo);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        requestHandleInfos.sort(RequestHandleInfo::compareTo);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        doRequestMapping(RequestType.DELETE, req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doRequestMapping(RequestType.GET, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doRequestMapping(RequestType.POST, req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        doRequestMapping(RequestType.PUT, req, resp);
    }

    private void doRequestMapping(RequestType requestType, HttpServletRequest req, HttpServletResponse resp) {
        String mappingUrl = getMappingUrl(req);
        for (RequestHandleInfo info : requestHandleInfos) {
            if (!pathMatcher.match(info.getMappingPath(), mappingUrl) && info.getRequestType() == requestType)
                continue;
            RequestParamHolder requestParams = info.validate(mappingUrl, req, resp);
            if (requestParams == null)
                continue;
            Object result = info.handle(requestParams);
            if (result == null)
                continue;
            writeResponse(resp, result);
            break;
        }
    }

    private Map<String, Object> parsePathVariable(String path) {
        // TODO:
        return null;
    }

    private void writeResponse(HttpServletResponse resp, Object result) {
        // TODO:这里还要有跳转的逻辑
        if (result instanceof String str) {
            try {
                resp.getWriter().write(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //TODO: 转换成json
        }
    }

    private String getMappingUrl(HttpServletRequest req) {
        // TODO: get mapping url
        return null;
    }

}
