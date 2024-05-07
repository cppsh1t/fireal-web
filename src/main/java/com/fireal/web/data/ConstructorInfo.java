package com.fireal.web.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import com.fireal.web.anno.RequestParam;
import com.fireal.web.core.WebInitializer;
import com.fireal.web.exception.RequestParamInfoException;

import fireal.structure.Tuple;

public class ConstructorInfo {

    static class ParameterInfo {
        private String name;
        private String defaultValue;
        private Class<?> type;

        public ParameterInfo(String name, String defaultValue, Class<?> type) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public Class<?> getType() {
            return type;
        }

    }

    private Class<?> targetClass;
    private List<Tuple<List<ParameterInfo>, Constructor<?>>> infos = new ArrayList<>();
    private Constructor<?> defaultConstructor = null;

    private ConstructorInfo(Class<?> clazz) {
        this.targetClass = clazz;
        Constructor<?>[] constructors = targetClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() > 0) {
                Tuple<List<ParameterInfo>, Constructor<?>> tuple = new Tuple<>(new ArrayList<>(), constructor);
                boolean constructorOK = true;
                for (Parameter parameter : constructor.getParameters()) {
                    RequestParam requestParamAnno = parameter.getAnnotation(RequestParam.class);
                    if (requestParamAnno == null) {
                        constructorOK = false;
                        break;
                    }
                    ParameterInfo parameterInfo = new ParameterInfo(requestParamAnno.value(),
                            requestParamAnno.defaultValue(), parameter.getType());
                    tuple.getFirstKey().add(parameterInfo);
                }
                if (constructorOK)
                    infos.add(tuple);
            } else {
                defaultConstructor = constructor;
            }
        }
        infos.sort((p1, p2) -> p2.getFirstKey().size() - p1.getFirstKey().size());
    }

    public Object build(Map<String, String> map) {
        for (var info : infos) {
            boolean legal = info.getFirstKey().stream().allMatch(p -> map.containsKey(p.name));
            if (!legal) continue;
            Constructor<?> constructor = info.getSecondKey();
            Object[] args = new Object[constructor.getParameterCount()];
            List<ParameterInfo> list = info.getFirstKey();
            for (int i = 0; i < args.length; i++) {
                ParameterInfo parameterInfo = list.get(i);
                String origin = map.getOrDefault(parameterInfo.name, parameterInfo.defaultValue);
                Object result = WebInitializer.stringToObject(origin, parameterInfo.getType());
                args[i] = result;
            }
            try {
                return constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                continue;
            }
        }
        try {
            return defaultConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeErrorException(null);//TODO: cant make
        }
    }

    public static ConstructorInfo make(Class<?> clazz) {
        ConstructorInfo constructorInfo = new ConstructorInfo(clazz);
        if (constructorInfo.defaultConstructor == null && constructorInfo.infos.size() == 0) {
            throw new RequestParamInfoException(clazz);// TODO: no right constructor
        }
        return constructorInfo;
    }
}
