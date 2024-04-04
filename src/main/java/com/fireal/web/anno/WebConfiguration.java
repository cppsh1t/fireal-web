package com.fireal.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebConfiguration {

    /**
     * The type of Container will use in web service
     *
     * @return The type of Container will use in web service
     */
    Class<?> value() default ElementType.class;

    boolean autoStart() default true;
}
