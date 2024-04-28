package com.fireal.web.exception;

public class RequestParamInfoException extends RuntimeException {
    public RequestParamInfoException(Class<?> clazz) {
        super("Can't build a RequestParamInfo which class is " + clazz + ".");
    }
}
