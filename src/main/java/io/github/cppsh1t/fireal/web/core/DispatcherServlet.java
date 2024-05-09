package io.github.cppsh1t.fireal.web.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.LoggerFactory;

import io.github.cppsh1t.fireal.web.anno.Order;
import io.github.cppsh1t.fireal.web.path.AntPathMatcher;
import io.github.cppsh1t.fireal.web.path.PathMatcher;
import io.github.cppsh1t.fireal.web.util.ReflectUtil;

import ch.qos.logback.classic.Logger;
import io.github.cppsh1t.fireal.core.Container;
import io.github.cppsh1t.fireal.definition.BeanDefinition;
import io.github.cppsh1t.fireal.util.BeanDefinitionUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {

    private static final Logger log = (Logger) LoggerFactory.getLogger(DispatcherServlet.class);

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
        log.info("Initializing DispatcherServlet...");
        Collection<BeanDefinition> beanDefinitions = BeanDefinitionUtil.getBeanDefinitions(container);
        for (var def : beanDefinitions) {
            if (!ReflectUtil.isHandler(def.getObjectType())) continue;
            log.debug("Found handler class: {}", def.getObjectType().getName());
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
                log.debug("Mapped method: {} to path: {}", method.getName(), mappingPath);
            }

        }

        requestHandleInfos.sort(RequestHandleInfo::compareTo);
        log.info("DispatcherServlet initialization complete.");
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
        log.debug("Handling request [{}] for URL: {}", requestType, mappingUrl);
        for (RequestHandleInfo info : requestHandleInfos) {
            if (matches(info, mappingUrl, requestType)) {
                handleRequest(info, mappingUrl, req, resp);
                return;
            }
        }
        log.warn("No matching handler found for request [{}] and URL: {}", requestType, mappingUrl);
    }

    private boolean matches(RequestHandleInfo info, String mappingUrl, RequestType requestType) {
        String justPath = mappingUrl.contains("?") ? mappingUrl.split("\\?")[0] : mappingUrl;
        log.debug("Match request {} with pattern {}", justPath, info.getMappingPath());
        boolean pathMatched = pathMatcher.match(info.getMappingPath(), justPath);
        boolean typeMatched = RequestType.containType(info.getRequestType(), requestType);
    
        if (pathMatched && typeMatched) {
            log.trace("Matched request [{}] to handler: {}", requestType, info);
        }
        return pathMatched && typeMatched;
    }

    private void handleRequest(RequestHandleInfo info, String mappingUrl, HttpServletRequest req, HttpServletResponse resp) {
        RequestParamHolder requestParamHolder = info.validate(mappingUrl, req, resp);
        if (requestParamHolder == null) {
            log.warn("Validation failed for request parameters.");
            return;
        }
    
        Object result = info.handle(requestParamHolder);
        if (result == null) {
            log.warn("Handler returned null, no response to write.");
            return;
        }
    
        writeResponse(resp, req, result);
    }

    private void writeResponse(HttpServletResponse resp, HttpServletRequest req, Object result) {
        if (result instanceof Router router) {
            String url = router.getUrl();
            Router.RouterType routerType = router.getRouterType();
            Router.recycle(router);
            try {
                if (routerType == Router.RouterType.FORWARD) {
                    RequestDispatcher dispatcher = req.getRequestDispatcher(url);
                    log.debug("Forwarding request to: {}", url);
                    dispatcher.forward(req, resp);
                } else {
                    log.debug("Redirecting to: {}", url);
                    resp.sendRedirect(url);
                }
            } catch (ServletException | IOException e) {
                log.error("Error forwarding/redirecting request: ", e);
            }
        }

        try {
            if (result instanceof String str) {
                resp.getWriter().write(str);
            } else {
                String json = WebInitializer.objectToJson(result);
                resp.getWriter().write(json);
            }
            log.debug("Wrote response to client.");
        } catch (IOException e) {
            log.error("Error writing response: ", e);
        }
    }

    public static String getMappingUrl(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().substring(contextPath.length());
        String queryString = req.getQueryString();
        return queryString == null ? requestURI : requestURI + "?" + queryString;
    }

}
