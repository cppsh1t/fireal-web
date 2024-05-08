package com.fireal.web.exception;

public class ParamterCastException extends RuntimeException {

    public ParamterCastException(String origin, Class<?> targetType) {
        super("Can't cast paramter origin string: [" + origin + "] to type " + targetType.getName() + ".");
    }
}
