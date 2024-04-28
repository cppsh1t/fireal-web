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

public class RequestParamBuilder {

    //TODO: 暂时还不支持httpSelvetResponse等无注解参数的注入,所以现在按着顺序来可能有问题

    public Collection<RequestParamInfo> build(Method method) {
        if (method.getParameterCount() == 0) return null;

        List<RequestParamInfo> params = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            Annotation annotation = Arrays.stream(parameter.getDeclaredAnnotations())
                .filter(anno -> anno.annotationType().isAnnotationPresent(RequestParamType.class))
                .findFirst().orElse(null);
            if (annotation == null) continue;//TODO: 正常情况下这个逻辑应该是去找无注解类型
            String name = ReflectUtil.invokeMethodInAnnotation(annotation, "value", null);
            String defaultValue = ReflectUtil.invokeMethodInAnnotation(annotation, "defaultValue", null);
            boolean required = ReflectUtil.invokeMethodInAnnotation(annotation, "required", null);
            RequestParamInfo requestParam = new RequestParamInfo(annotation.annotationType(), name, defaultValue, required);
            params.add(requestParam);
        }

        return params;
    }

}
