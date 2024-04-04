package com.fireal.web.anno;

import fireal.anno.ComponentType;
import fireal.definition.EmptyType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentType(forName = "value", forClass = "type")
public @interface Controller {

    String value() default "";

    Class<?> type() default EmptyType.class;
}
