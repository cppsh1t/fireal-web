package io.github.cppsh1t.fireal.web.core;

public enum RequestType {

    ALL,
    GET,
    POST,
    PUT,
    DELETE;

    public static boolean containType(RequestType source, RequestType other) {
        if (source == RequestType.ALL) return true;
        return source == other;
    }
}
