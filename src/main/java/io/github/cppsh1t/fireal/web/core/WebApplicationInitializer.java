package io.github.cppsh1t.fireal.web.core;

import io.github.cppsh1t.fireal.web.data.JsonConverter;

import io.github.cppsh1t.fireal.core.Container;


public interface WebApplicationInitializer {

    /**
     * Get config class of webApplication will use
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
     * Get container class of webApplication will use
     * @return Class of container
     */
    default Class<? extends Container> getContainerClass() {
        return WebAppContainer.class;
    } 
} 
