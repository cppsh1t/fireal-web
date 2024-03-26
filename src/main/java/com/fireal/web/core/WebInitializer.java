package com.fireal.web.core;

import jakarta.servlet.*;

import java.util.Set;

import com.fireal.web.anno.WebConfiguration;
import com.fireal.web.exception.WebInitializationException;

public class WebInitializer implements ServletContainerInitializer {

    
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        Class<?> configClass = null;
        for(Class<?> clazz : set) {
            if (clazz.isAnnotationPresent(WebConfiguration.class)) {
                configClass = clazz;
                break;
            }
        }
        if (configClass == null) {
            throw new WebInitializationException("Can't find a class with WebConfiguration.");
        }
        
        // ServletRegistration.Dynamic servlet = ctx.addServlet("servletContainer", new ContainerServlet());
        // servlet.addMapping("/");
        // servlet.setLoadOnStartup(0);

        // FilterRegistration.Dynamic filter = ctx.addFilter("mainFilter", new MainFilter());
        // filter.addMappingForUrlPatterns(null, true, "/*");
    }
}