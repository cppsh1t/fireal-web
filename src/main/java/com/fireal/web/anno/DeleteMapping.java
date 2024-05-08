package com.fireal.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declaring this method to handle DELETE requests
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DeleteMapping {

    /**
     * The pattern of mapping
     * @return The pattern of mapping
     */
    String value();

    /**
     * The paramters of handler need
     * @return The paramters of handler need
     */
    String[] params() default {};

    /**
     * The produces mode of handler
     * @return The produces mode of handler
     */
    String produces() default "";
}