package io.github.cppsh1t.fireal.web.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import io.github.cppsh1t.fireal.web.anno.DeleteMapping;
import io.github.cppsh1t.fireal.web.anno.GetMapping;
import io.github.cppsh1t.fireal.web.anno.PostMapping;
import io.github.cppsh1t.fireal.web.anno.PutMapping;
import io.github.cppsh1t.fireal.web.anno.RequestMapping;
import io.github.cppsh1t.fireal.web.core.RequestType;
import io.github.cppsh1t.fireal.definition.BeanDefinitionParser;
import io.github.cppsh1t.fireal.definition.DefaultBeanDefinitionParser;

public class ReflectUtil {

    private static final BeanDefinitionParser parser = new DefaultBeanDefinitionParser();

    public static boolean isHandler(Class<?> clazz) {
        if (!isAnnotatedRequestMapping(clazz)) return false;
        return parser.getBeanAnno(clazz) != null;
    }

    public static Collection<Method> getRequestMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(ReflectUtil::isAnnotatedRequestMapping).toList();
    }

    public static RequestType getRequestType(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) return RequestType.ALL;
        if (method.isAnnotationPresent(GetMapping.class)) return RequestType.GET;
        if (method.isAnnotationPresent(PostMapping.class)) return RequestType.POST;
        if (method.isAnnotationPresent(PutMapping.class)) return RequestType.PUT;
        if (method.isAnnotationPresent(DeleteMapping.class)) return RequestType.DELETE;
        return null;
    }

    public static String getMappingPath(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) return method.getAnnotation(RequestMapping.class).value();
        if (method.isAnnotationPresent(GetMapping.class)) return method.getAnnotation(GetMapping.class).value();
        if (method.isAnnotationPresent(PostMapping.class)) return method.getAnnotation(PostMapping.class).value();
        if (method.isAnnotationPresent(PutMapping.class)) return method.getAnnotation(PutMapping.class).value();
        if (method.isAnnotationPresent(DeleteMapping.class)) return method.getAnnotation(DeleteMapping.class).value();
        return null;
    }

    public static String getMappingPath(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RequestMapping.class)) return clazz.getAnnotation(RequestMapping.class).value();
        if (clazz.isAnnotationPresent(GetMapping.class)) return clazz.getAnnotation(GetMapping.class).value();
        if (clazz.isAnnotationPresent(PostMapping.class)) return clazz.getAnnotation(PostMapping.class).value();
        if (clazz.isAnnotationPresent(PutMapping.class)) return clazz.getAnnotation(PutMapping.class).value();
        if (clazz.isAnnotationPresent(DeleteMapping.class)) return clazz.getAnnotation(DeleteMapping.class).value();
        return null;
    }

    public static String[] getMappingLimit(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) return method.getAnnotation(RequestMapping.class).params();
        if (method.isAnnotationPresent(GetMapping.class)) return method.getAnnotation(GetMapping.class).params();
        if (method.isAnnotationPresent(PostMapping.class)) return method.getAnnotation(PostMapping.class).params();
        if (method.isAnnotationPresent(PutMapping.class)) return method.getAnnotation(PutMapping.class).params();
        if (method.isAnnotationPresent(DeleteMapping.class)) return method.getAnnotation(DeleteMapping.class).params();
        return null;
    }

    public static boolean isAnnotatedRequestMapping(Class<?> clazz) {
        return isAnnotationsPresentAny(clazz, RequestMapping.class, PostMapping.class, PutMapping.class, GetMapping.class, DeleteMapping.class);
    }

    public static boolean isAnnotatedRequestMapping(AccessibleObject obj) {
        return isAnnotationsPresentAny(obj, RequestMapping.class, PostMapping.class, PutMapping.class, GetMapping.class, DeleteMapping.class);
    }

    @SafeVarargs
    public static boolean isAnnotationsPresentAny(Class<?> clazz, Class<? extends Annotation>... annoClasses) {
        for(Class<? extends Annotation> annoClass : annoClasses) {
            if (clazz.isAnnotationPresent(annoClass)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static boolean isAnnotationsPresentAny(AccessibleObject obj, Class<? extends Annotation>... annoClasses) {
        for(Class<? extends Annotation> annoClass : annoClasses) {
            if (obj.isAnnotationPresent(annoClass)) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethodInAnnotation(Annotation annotation, String methodName, Object[] args) {
        Method method;
        try {
            method = annotation.getClass().getMethod(methodName);
            return (T) method.invoke(annotation, args);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

    }

}
