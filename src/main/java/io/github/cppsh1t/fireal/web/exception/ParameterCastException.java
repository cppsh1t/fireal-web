package io.github.cppsh1t.fireal.web.exception;

public class ParameterCastException extends RuntimeException {

    public ParameterCastException(String origin, Class<?> targetType) {
        super("Can't cast parameter origin string: [" + origin + "] to type " + targetType.getName() + ".");
    }
}
