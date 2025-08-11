package com.example.demo.helpers;

import java.lang.reflect.Field;
import java.util.List;

public class GenericFieldAccessor {

    public static <T> String getFieldValue(T obj, String fieldName) {
        try {
            Field field = getField(obj.getClass(), fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field '" + fieldName + "': " + e.getMessage(), e);
        }
    }

    public static <T> void setFieldValue(T obj, String fieldName, Object value) {
        try {
            Field field = getField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "': " + e.getMessage(), e);
        }
    }

    public static <T> boolean isDuplicateField(List<T> entities, String field, Object value, Object currentId) {
        return entities.stream()
                .filter(entity -> {
                    String idStr = getFieldValue(entity, "id");
                    return idStr != null && !idStr.equals(currentId.toString());
                })
                .anyMatch(entity -> {
                    String fieldValue = getFieldValue(entity, field);
                    return fieldValue != null && fieldValue.equals(value.toString());
                });
    }

    private static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + name + "' not found in class hierarchy");
    }
}
