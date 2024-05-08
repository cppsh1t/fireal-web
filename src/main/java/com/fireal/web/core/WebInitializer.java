package com.fireal.web.core;

import com.fireal.web.util.TypeUtil;
import jakarta.servlet.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.fireal.web.data.JsonConverter;
import com.fireal.web.exception.WebInitializationException;

import fireal.core.Container;
import jakarta.servlet.annotation.HandlesTypes;

@HandlesTypes(WebApplicationInitializer.class)
public class WebInitializer implements ServletContainerInitializer {

    private static JsonConverter defaultJsonConverter;
    private static final Map<Class<?>, JsonConverter> jsonConverterMap = new HashMap<>();
    private static Container container;

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext ctx) {
        if (set == null || set.size() == 0) {
            throw new WebInitializationException("Can't find a class implements WebApplicationInitializer.");
        }
        Class<?> initClass = set.stream().findFirst().orElse(null);

        WebApplicationInitializer initializer;
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
        Constructor<? extends Container> constructorOfContainer;
        try {
            constructorOfContainer = containerClass.getConstructor(Class.class);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new WebInitializationException("Can't find right constructor on " + containerClass);
        }

        try {
            container = constructorOfContainer.newInstance(initializer.getConfigClass());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new WebInitializationException("Can't make instance of " + containerClass);
        }

        ServletRegistration.Dynamic servlet = ctx.addServlet("dispatcherServlet", new DispatcherServlet(container));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(0);

        FilterRegistration.Dynamic filter = ctx.addFilter("mainFilter", new InterceptorFilter());
        filter.addMappingForUrlPatterns(null, true, "/*");
    }

    public static String objectToJson(Object obj) {
        Class<?> type = obj.getClass();
        if (jsonConverterMap.containsKey(type)) {
            return jsonConverterMap.get(type).objectToJson(obj);
        }
        return defaultJsonConverter.objectToJson(obj);
    }

    public static Object stringToObject(String str, Class<?> targetType) {
        if (jsonConverterMap.containsKey(targetType)) {
            return jsonConverterMap.get(targetType).stringToObject(str);
        } else {
            return TypeUtil.castString(str, targetType);
        }
    }

    public static Container getContainer() {
        return container;
    }
}