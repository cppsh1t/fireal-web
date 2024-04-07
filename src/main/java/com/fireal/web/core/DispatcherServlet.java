package com.fireal.web.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Collection;

import com.fireal.web.util.ReflectUtil;

import fireal.core.Container;
import fireal.definition.BeanDefinition;
import fireal.util.BeanDefinitionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

public class DispatcherServlet extends HttpServlet{

    private Container container;

    public DispatcherServlet(Container container) {
        this.container = container;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Collection<BeanDefinition> beanDefinitions = BeanDefinitionUtil.getBeanDefinitions(container);
        Lookup lookup = MethodHandles.lookup();

        for(var def : beanDefinitions) {
            if (!ReflectUtil.isHandler(def.getObjectType())) continue;
            Collection<Method> methods = ReflectUtil.getRequestMethods(def.getObjectType());
            if (methods == null) continue;

            for(Method method : methods) {
                RequestType requestType = ReflectUtil.getRequestType(method);
                String mappingPath = ReflectUtil.getMappingPath(method);
                try {
                    MethodHandle methodHandle = lookup.unreflect(method);
                    methodHandle = methodHandle.bindTo(container.getBean(def.getKeyType()));

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }


    }



}
