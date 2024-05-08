package io.github.cppsh1t.fireal.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@RequestParamType
public @interface PathVariable {

    /**
     * The parameter's name
     * @return
     */
    String value();
}