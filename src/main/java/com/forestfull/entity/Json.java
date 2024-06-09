package com.forestfull.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forestfull.handler.JsonTypeHandler;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Json extends LinkedHashMap<String, Object> {

    @Override
    public String toString() {
        try {
            return JsonTypeHandler.writer.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class List extends LinkedList<Json> {
        @Override
        public String toString() {
            try {
                return JsonTypeHandler.writer.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}