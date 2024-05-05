package com.fireal.web.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@RequestParamType
public @interface ModelParam {

    //TODO: use this to construct complex object in inject params when process mapping url
    String[] value();
}
