package io.github.cppsh1t.fireal.web.exception;

public class RequestParamInfoException extends RuntimeException {

    public RequestParamInfoException() {
        super("Can't build RequestParamInfo.");
    }

    public RequestParamInfoException(Class<?> clazz) {
        super("Can't build RequestParamInfo which class is " + clazz + ".");
    }
}
