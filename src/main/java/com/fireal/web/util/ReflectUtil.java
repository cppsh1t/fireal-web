package com.fireal.web.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import com.fireal.web.anno.Controller;
import com.fireal.web.anno.DeleteMapping;
import com.fireal.web.anno.GetMapping;
import com.fireal.web.anno.PostMapping;
import com.fireal.web.anno.PutMapping;
import com.fireal.web.anno.RequestMapping;
import com.fireal.web.core.RequestType;
import fireal.definition.BeanDefinitionParser;
import fireal.definition.DefaultBeanDefinitionParser;

public class ReflectUtil {

    private static final BeanDefinitionParser parser = new DefaultBeanDefinitionParser();

    public static boolean isHandler(Class<?> clazz) {
        if (!isAnnotatedRequestMapping(clazz)) return false;
        if (parser.getBeanAnno(clazz) != null) return true;
        return false;
    }

    public static Collection<Method> getRequestMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods()).filter(m -> isAnnotatedRequestMapping(m)).toList();
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

    @SuppressWarnings("unchecked")
    public static boolean isAnnotatedRequestMapping(Class<?> clazz) {
        return isAnnotationsPresent(clazz, RequestMapping.class, PostMapping.class, PutMapping.class, GetMapping.class, DeleteMapping.class);
    }

    @SuppressWarnings("unchecked")
    public static boolean isAnnotatedRequestMapping(AccessibleObject obj) {
        return isAnnotationsPresent(obj, RequestMapping.class, PostMapping.class, PutMapping.class, GetMapping.class, DeleteMapping.class);
    }

    public static boolean isAnnotationsPresent(Class<?> clazz, @SuppressWarnings("unchecked") Class<? extends Annotation>... annoClassess) {
        for(Class<? extends Annotation> annoClass : annoClassess) {
            if (clazz.isAnnotationPresent(annoClass)) continue;
            return false;
        }
        return true;
    }

    public static boolean isAnnotationsPresent(AccessibleObject obj, @SuppressWarnings("unchecked") Class<? extends Annotation>... annoClassess) {
        for(Class<? extends Annotation> annoClass : annoClassess) {
            if (obj.isAnnotationPresent(annoClass)) continue;
            return false;
        }
        return true;
    }

}