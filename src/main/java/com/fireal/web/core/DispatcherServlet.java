package com.fireal.web.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.fireal.web.anno.Order;
import com.fireal.web.path.AntPathMatcher;
import com.fireal.web.path.PathMatcher;
import com.fireal.web.util.DebugUtil;
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
    
    private void doRequestMapping(RequestType requestType, HttpServletRequest req, HttpServletResponse resp) {
        String mappingUrl = getMappingUrl(req);
        DebugUtil.log("正在处理请求", mappingUrl);
        for (RequestHandleInfo info : requestHandleInfos) {
            DebugUtil.log("对比模式", info.getMappingPath());
            String justPath = mappingUrl.contains("?") ? mappingUrl.split("\\?")[0] : mappingUrl;
            if (!(pathMatcher.match(info.getMappingPath(), justPath)
                    && RequestType.containType(info.getRequestType(), requestType)))
                continue;
            DebugUtil.log("找到对应的Handler", info);
            RequestParamHolder requestParamHolder = info.validate(mappingUrl, req, resp);
            if (requestParamHolder == null)
                continue;
            DebugUtil.log("参数处理完成", requestParamHolder);
            Object result = info.handle(requestParamHolder);
            if (result == null)
                continue;
            DebugUtil.log("处理结果", result);
            writeResponse(resp, req, result);
            break;
        }
    }

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

    public static String getMappingUrl(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().substring(contextPath.length());
        String queryString = req.getQueryString();
        return queryString == null ? requestURI : requestURI + "?" + queryString;
    }

}
