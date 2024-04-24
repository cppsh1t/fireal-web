package com.fireal.web.core;

import java.lang.annotation.Annotation;

public interface RequestParamProcessor {
    Class<?> getRequestParamType();
    RequestParam makeRequestParam(Annotation annotation);
} 