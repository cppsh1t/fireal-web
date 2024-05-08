package io.github.cppsh1t.fireal.web.anno;


import java.lang.annotation.*;

/**
 * The order of request handler
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    int value() default 0;
}
