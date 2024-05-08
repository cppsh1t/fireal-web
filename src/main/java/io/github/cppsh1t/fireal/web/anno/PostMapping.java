package io.github.cppsh1t.fireal.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declaring this method to handle POST requests
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface PostMapping {

    /**
     * The pattern of mapping
     * 
     * @return The pattern of mapping
     */
    String value();

    /**
     * The parameters of handler need
     * 
     * @return The parameters of handler need
     */
    String[] params() default {};

    /**
     * The produces mode of handler
     * 
     * @return The produces mode of handler
     */
    String produces() default "";
}