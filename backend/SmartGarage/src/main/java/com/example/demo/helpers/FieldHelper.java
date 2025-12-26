package com.example.demo.helpers;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class FieldHelper {
    // This method can check duplicates for ANY type of object
    public static <T, ID> boolean hasDuplicateField(
            List<T> entities,               // List of objects (Users, Products, etc.)
            Function<T, String> fieldGetter, // How to get the field value (like User::getEmail)
            String value,                   // Value we're checking
            ID currentEntityId,             // ID of current object (to exclude it)
            Function<T, ID> idExtractor) {  // How to get the ID (like User::getId)

        return entities.stream()
                // Exclude the current object (the one being updated)
                .filter(entity -> !currentEntityId.equals(idExtractor.apply(entity)))
                // Get the field value for each object
                .map(fieldGetter)
                // Check if any matches our value
                .anyMatch(fieldValue -> fieldValue != null && fieldValue.equals(value));
    }

    public static <T> void updateFieldIfChanged(
            T item,                          // The object to update
            Function<T, String> currentValueGetter, // Get current value
            BiConsumer<T, String> setter,    // Set new value
            String newValue) {               // Value to set

        // Get current value
        String currentValue = currentValueGetter.apply(item);

        // Only update if new value is different
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(item, newValue);
        }
    }

    public static <T, ID> void updateWithDuplicateCheck(
            T item,
            List<T> allItems,
            Function<T, String> getter,
            BiConsumer<T, String> setter,
            String newValue,
            ID itemId,
            Function<T, ID> idGetter,
            String fieldName) {

        // Get current value
        String currentValue = getter.apply(item);

        // If value is changing
        if (newValue != null && !newValue.equals(currentValue)) {
            // Check for duplicates
            if (hasDuplicateField(allItems, getter, newValue, itemId, idGetter)) {
                throw new RuntimeException(
                        String.format("%s '%s' already exists", fieldName, newValue));
            }
            // Set new value
            setter.accept(item, newValue);
        }
    }

}
