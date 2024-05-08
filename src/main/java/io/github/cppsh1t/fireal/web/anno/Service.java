package io.github.cppsh1t.fireal.web.anno;

import io.github.cppsh1t.fireal.definition.EmptyType;

import java.lang.annotation.*;

/**
 * Declaring this class as a Service
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    Class<?> value() default EmptyType.class;

    String name() default "";
}
