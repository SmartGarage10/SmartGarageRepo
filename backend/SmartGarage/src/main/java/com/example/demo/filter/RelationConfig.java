package com.example.demo.filter;

import java.util.List;
import java.util.function.Function;

public record RelationConfig(List<String> path,
                             String targetField,
                             Function<Object, Object> transformer) {

    public static RelationConfig of(List<String> path, String targetField) {
        return new RelationConfig(path, targetField, null);
    }

    public static RelationConfig of(List<String> path, String targetField, Function<Object, Object> transformer) {
        return new RelationConfig(path, targetField, transformer);
    }

    // Additional factory method for convenience (if you want to use strings)
    public static RelationConfig of(String targetField, Function<Object, Object> transformer) {
        return new RelationConfig(List.of(), targetField, transformer);
    }
}