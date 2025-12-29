package com.example.demo.helpers;

import java.util.function.BiConsumer;
import java.util.function.Function;

// Generic with two type parameters: T (entity), V (field value)
public record FieldUpdateHelper<T, V>(
        String fieldName,
        Function<T, V> getter,
        BiConsumer<T, V> setter,
        V newValue
) {
}