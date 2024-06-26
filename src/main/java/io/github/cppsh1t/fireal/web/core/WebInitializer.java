package io.github.cppsh1t.fireal.web.core;

import ch.qos.logback.classic.Logger;
import io.github.cppsh1t.fireal.web.util.TypeUtil;
import io.github.cppsh1t.fireal.web.exception.WebInitializationException;
import jakarta.servlet.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import io.github.cppsh1t.fireal.web.data.JsonConverter;

import io.github.cppsh1t.fireal.core.Container;
import jakarta.servlet.annotation.HandlesTypes;
import org.slf4j.LoggerFactory;

@HandlesTypes(WebApplicationInitializer.class)
public class WebInitializer implements ServletContainerInitializer {

    private static JsonConverter defaultJsonConverter;
    private static final Map<Class<?>, JsonConverter> jsonConverterMap = new HashMap<>();
    private static Container container;
    private static final Logger log = (Logger) LoggerFactory.getLogger(DispatcherServlet.class);

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
        } catch (Exception e) {
            log.error("Error in construct container", e);
            throw new WebInitializationException("Can't make instance of " + containerClass);
        }

        ServletRegistration.Dynamic servlet = ctx.addServlet("dispatcherServlet", new DispatcherServlet(container));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(0);

        FilterRegistration.Dynamic filter = ctx.addFilter("interceptorFilter", new InterceptorFilter());
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