package com.fireal.web.anno;

import fireal.definition.EmptyType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    Class<?> value() default EmptyType.class;

    String name() default "";
}
