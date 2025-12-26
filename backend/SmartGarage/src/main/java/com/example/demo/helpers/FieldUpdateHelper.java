package com.example.demo.helpers;

import com.example.demo.models.User;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record FieldUpdateHelper(String fieldName, Function<User, String> getter, BiConsumer<User, String> setter,
                                String newValue) {
}
