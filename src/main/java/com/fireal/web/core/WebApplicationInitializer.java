package com.fireal.web.core;

import com.fireal.web.data.JsonConverter;

import fireal.core.Container;


public interface WebApplicationInitializer {

    /**
     * Get config class of WebApplication will use
     * @return config class
     */
    Class<?> getConfigClass();

    /**
     * Get defaultJsonConverter which used in process the result of handle request
     * @return defaultJsonConverter
     */
    default JsonConverter getDefaultJsonConverter() {
        return new JsonConverter.DefaultJsonConverter();
    }

    /**
     * Get jsonConverter of special type which used in process the result of handle request
     * @return jsonConverter of special type
     */
    default JsonConverter[] appendJsonConverter() {
        return null;
    }

    /**
     * Get Container Class of WebApplication will use
     * @return
     */
    default Class<? extends Container> getContainerClass() {
        return WebAppContainer.class;
    } 

    default Class<?> getSecurityClass() {
        return null;
    }
} 
