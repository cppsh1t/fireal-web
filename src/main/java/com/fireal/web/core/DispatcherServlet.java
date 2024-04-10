package com.fireal.web.core;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class DispatcherServlet extends HttpServlet{

    private Container container;
    private List<RequestHandleInfo> requestHandleInfos = new ArrayList<>();
    private PathMatcher pathMatcher = new AntPathMatcher();

    public DispatcherServlet(Container container) {
        this.container = container;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Collection<BeanDefinition> beanDefinitions = BeanDefinitionUtil.getBeanDefinitions(container);
        Lookup lookup = MethodHandles.lookup();

        for(var def : beanDefinitions) {
            if (!ReflectUtil.isHandler(def.getObjectType())) continue;
            Collection<Method> methods = ReflectUtil.getRequestMethods(def.getObjectType());
            if (methods == null || methods.size() == 0) continue;

            for(Method method : methods) {
                RequestType requestType = ReflectUtil.getRequestType(method);
                String mappingPath = ReflectUtil.getMappingPath(method);
                try {
                    MethodHandle methodHandle = lookup.unreflect(method);
                    methodHandle = methodHandle.bindTo(container.getBean(def.getKeyType()));
                    requestHandleInfos.add(new RequestHandleInfo(methodHandle, requestType, mappingPath));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequestMapping(RequestType.DELETE, req,   resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequestMapping(RequestType.GET, req,   resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequestMapping(RequestType.POST, req,   resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequestMapping(RequestType.PUT, req,   resp);
    }


    private void doRequestMapping(RequestType requestType, HttpServletRequest req, HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        String mappingUrl = getMappingUrl(req);
        for (RequestHandleInfo requestHandleInfo : requestHandleInfos) {
            
        }
    }

    private String getMappingUrl(HttpServletRequest req) {
        //TODO: get mapping url
        return null;
    }

}
