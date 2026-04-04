package com.example.demo.helpers;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class FieldHelper {

    // Generic version for ANY type
    public static <T, ID, V> boolean hasDuplicateField(
            List<T> entities,
            Function<T, V> fieldGetter,
            V value,
            ID currentEntityId,
            Function<T, ID> idExtractor) {

        return entities.stream()
                .filter(entity -> !currentEntityId.equals(idExtractor.apply(entity)))
                .map(fieldGetter)
                .anyMatch(fieldValue -> fieldValue != null && fieldValue.equals(value));
    }

    // Generic version for ANY type
    public static <T, V> void updateFieldIfChanged(
            T item,
            Function<T, V> currentValueGetter,  // Generic V
            BiConsumer<T, V> setter,            // Generic V
            V newValue) {                       // Generic V

        V currentValue = currentValueGetter.apply(item);

        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(item, newValue);
        }
    }

    // Generic version for ANY type
    public static <T, ID, V> void updateWithDuplicateCheck(
            T item,
            List<T> allItems,
            Function<T, V> getter,
            BiConsumer<T, V> setter,
            V newValue,
            ID itemId,
            Function<T, ID> idGetter,
            String fieldName) {

        V currentValue = getter.apply(item);

        if (newValue != null && !newValue.equals(currentValue)) {
            if (hasDuplicateField(allItems, getter, newValue, itemId, idGetter)) {
                throw new RuntimeException(
                        String.format("%s '%s' already exists", fieldName, newValue));
            }
            setter.accept(item, newValue);
        }
    }
}