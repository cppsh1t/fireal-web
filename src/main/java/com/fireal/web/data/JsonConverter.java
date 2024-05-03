package com.fireal.web.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public interface JsonConverter {

    Class<?> getObjectType();

    String objectToJson(Object object);
    
    Object jsonToObject(String json);

    Object stringToObject(Map<String, String> originStrings, Class<?> targetType);

    class DefaultJsonConverter implements JsonConverter{

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Class<?> getObjectType() {
            return Object.class;
        }

        @Override
        public String objectToJson(Object object) {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                return e.getMessage();
            }
        }

        @Override
        public Object jsonToObject(String json) {
            throw new UnsupportedOperationException("Unsupport to convert json to Object.");
        }

        @Override
        public Object stringToObject(Map<String, String> originStrings, Class<?> targetType) {
            // TODO 实现stringToObject
            return null;
        }
        
    }
} 