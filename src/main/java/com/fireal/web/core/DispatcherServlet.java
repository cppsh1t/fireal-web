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
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {

    private final Container container;
    private final List<RequestHandleInfo> requestHandleInfos = new ArrayList<>();
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final RequestParamBuilder requestParamBuilder = new RequestParamBuilder();

    public DispatcherServlet(Container container) {
        this.container = container;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Collection<BeanDefinition> beanDefinitions = BeanDefinitionUtil.getBeanDefinitions(container);
        for (var def : beanDefinitions) {
            if (!ReflectUtil.isHandler(def.getObjectType())) continue;
            Collection<Method> methods = ReflectUtil.getRequestMethods(def.getObjectType());
            if (methods == null || methods.size() == 0) continue;

            String handlerMappingPath = ReflectUtil.getMappingPath(def.getObjectType());
            Object invoker = container.getBean(def.getKeyType());
            for (Method method : methods) {
                RequestType requestType = ReflectUtil.getRequestType(method);
                String mappingPath = ReflectUtil.getMappingPath(method);
                mappingPath = handlerMappingPath + mappingPath;
                int order = 0;
                if (method.isAnnotationPresent(Order.class)) {
                    order = method.getAnnotation(Order.class).value();
                }
                RequestHandleInfo requestHandleInfo =
                        new RequestHandleInfo(method, invoker, requestType, mappingPath, order);
                Collection<RequestParamInfo> params = requestParamBuilder.build(method);
                String[] limits = ReflectUtil.getMappingLimit(method);
                requestHandleInfo.addRequestMappingLimit(limits);
                if (params != null) requestHandleInfo.addRequestParam(params);
                requestHandleInfos.add(requestHandleInfo);
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

    //FIXME: 映射关系有错误
    private void doRequestMapping(RequestType requestType, HttpServletRequest req, HttpServletResponse resp) {
        String mappingUrl = getMappingUrl(req);
        for (RequestHandleInfo info : requestHandleInfos) {
            if (!pathMatcher.match(info.getMappingPath(), mappingUrl) && info.getRequestType() == requestType)
                continue;
            RequestParamHolder requestParamHolder = info.validate(mappingUrl, req, resp);
            if (requestParamHolder == null)
                continue;
            Object result = info.handle(requestParamHolder);
            if (result == null)
                continue;
            writeResponse(resp, req, result);
            break;
        }
    }

    private Map<String, Object> parsePathVariable(String path) {
        // TODO:
        return null;
    }

    //TODO: 还要有传输值类型的判定
    private void writeResponse(HttpServletResponse resp, HttpServletRequest req, Object result) {
        if (result instanceof Router router) {
            String url = router.getUrl();
            Router.RouterType routerType = router.getRouterType();
            Router.recycle(router);
            try {
                if (routerType == Router.RouterType.FORWARD) {
                    RequestDispatcher dispatcher = req.getRequestDispatcher(url);
                    dispatcher.forward(req, resp);
                } else {
                    resp.sendRedirect(url);
                }
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (result instanceof String str) {
                resp.getWriter().write(str);
            } else {
                String json = WebInitializer.objectToJson(result);
                resp.getWriter().write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMappingUrl(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().substring(contextPath.length());
        String queryString = req.getQueryString();
        return queryString == null ? requestURI : requestURI + "?" + queryString;
    }

}
