package com.fireal.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intercepting query parameters from the request as arguments
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@RequestParamType
public @interface RequestParam {

    /**
     * The paramter's name
     * @return
     */
    String value();

    /**
     * The necessity of parameters. If not, and the corresponding parameter is absent in the query, a default value will be used instead.
     * @return The necessity of parameters.
     */
    boolean required() default true;

    /**
     * The defaultValue of paramter
     * @return
     */
    String defaultValue() default "";
}