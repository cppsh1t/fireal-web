package com.fireal.web.core;

import com.fireal.web.util.TypeUtil;
import jakarta.servlet.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

import com.fireal.web.data.JsonConverter;
import com.fireal.web.exception.WebInitializationException;

import fireal.core.Container;
import jakarta.servlet.annotation.HandlesTypes;

@HandlesTypes(WebApplicationInitializer.class)
public class WebInitializer implements ServletContainerInitializer {

    private static JsonConverter defaultJsonConverter;
    private static final Map<Class<?>, JsonConverter> jsonConverterMap = new HashMap<>();

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

        Container container;
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

    //FIXME: 只能做一层的构造
    //TODO: 按参数名字构造好像不对
    public static Object constructObject(Map<String, String> map, Class<?> targetType) {
        List<Constructor<?>> constructors = Arrays.stream(targetType.getConstructors())
                .filter(con -> Arrays.stream(con.getParameters()).map(Parameter::getName)
                        .allMatch(map::containsKey))
                .sorted((c1, c2) -> c2.getParameterCount() - c1.getParameterCount())
                .toList();
        for(Constructor<?> constructor : constructors) {
            Object[] args = new Object[constructor.getParameterCount()];
            Parameter[] parameters = constructor.getParameters();
            try {
                for (int i = 0; i < args.length; i++) {
                    Parameter parameter = parameters[i];
                    String name = parameter.getName();
                    args[i] = WebInitializer.stringToObject(map.get(name), parameter.getType());
                }
                return constructor.newInstance(args);
            } catch (ClassCastException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        throw new RuntimeException();//TODO: can't construct
    }
}