package com.fireal.web.core;

import com.fireal.web.data.JsonConverter;

import fireal.core.Container;


public interface WebApplicationInitializer {

    Class<?> getConfigClass();

    default JsonConverter getDeafultJsonConverter() {
        return new JsonConverter.DefaultJsonConverter();
    }

    default JsonConverter[] appendJsonConverter() {
        return null;
    }

    default Class<? extends Container> getContainerClass() {
        return WebAppContainer.class;
    } 
} 
