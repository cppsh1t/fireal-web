package com.fireal.web.core;

import jakarta.servlet.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fireal.web.data.JsonConverter;
import com.fireal.web.exception.WebInitializationException;

import fireal.core.Container;

public class WebInitializer implements ServletContainerInitializer {

    private static JsonConverter defaultJsonConverter;
    private static Map<Class<?>, JsonConverter> jsonConverterMap = new HashMap<>();

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) throws ServletException {
        Class<?> initClass = null;
        for (Class<?> clazz : set) {
            if (WebApplicationInitializer.class.isAssignableFrom(clazz)) {
                initClass = clazz;
                break;
            }
        }

        if (initClass == null) {
            throw new WebInitializationException("Can't find a class implements WebApplicationInitializer.");
        }

        WebApplicationInitializer initializer = null;
        try {
            initializer = (WebApplicationInitializer) initClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new WebInitializationException("Can't make instance of " + initClass);
        }

        defaultJsonConverter = initializer.getDefaultJsonConverter();
        JsonConverter[] converters = initializer.appendJsonConverter();
        if (converters != null) {
            for (JsonConverter converter : converters) {
                jsonConverterMap.put(converter.getObjectType(), converter);
            }
        }

        Class<? extends Container> containerClass = initializer.getContainerClass();
        Constructor<? extends Container> constructorOfContainer = null;
        try {
            constructorOfContainer = containerClass.getConstructor(Class.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new WebInitializationException("Can't find right constructor on " + containerClass);
        }

        Container container = null;
        try {
            container = constructorOfContainer.newInstance(initializer.getConfigClass());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new WebInitializationException("Can't make instance of " + containerClass);
        }

        ServletRegistration.Dynamic servlet = ctx.addServlet("dispatcherServlet", new DispatcherServlet(container));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(0);

        FilterRegistration.Dynamic filter = ctx.addFilter("mainFilter", new MainFilter());
        filter.addMappingForUrlPatterns(null, true, "/*");
    }
}