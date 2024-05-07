package com.fireal.web.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fireal.web.anno.RequestParamType;
import com.fireal.web.util.ReflectUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RequestParamBuilder {

    public Collection<RequestParamInfo> build(Method method) {
        if (method.getParameterCount() == 0) return null;

        List<RequestParamInfo> params = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            Annotation annotation = Arrays.stream(parameter.getDeclaredAnnotations())
                .filter(anno -> anno.annotationType().isAnnotationPresent(RequestParamType.class))
                .findFirst().orElse(null);
            if (annotation != null) {
                String name = ReflectUtil.invokeMethodInAnnotation(annotation, "value", null);
                String defaultValue = ReflectUtil.invokeMethodInAnnotation(annotation, "defaultValue", null);
                boolean required = ReflectUtil.invokeMethodInAnnotation(annotation, "required", null);
                RequestParamInfo requestParam
                        = RequestParamInfo.simple(annotation.annotationType(), name, defaultValue, required, parameter.getType());
                params.add(requestParam);
            } else {
                Class<?> paramType = parameter.getType();
                if (paramType == HttpServletRequest.class || paramType == HttpServletResponse.class || paramType == HttpSession.class) {
                    params.add(RequestParamInfo.origin(paramType));
                } else {
                    params.add(RequestParamInfo.complex(parameter.getType()));
                }
            }

        }

        return params;
    }

}
