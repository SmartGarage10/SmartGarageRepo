package com.example.demo.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FilterHelper {
    private final ObjectMapper objectMapper;

    @Autowired
    public FilterHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public List<Filter> convertToFilters(MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyList();
        }

        // Special handling for JSON-encoded filters parameter
        if (params.containsKey("filters")) {
            try {
                String filtersJson = params.getFirst("filters");
                return objectMapper.readValue(filtersJson, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse filters parameter", e);
            }
        }

        // Default conversion for regular parameters
        return params.entrySet().stream()
                .filter(entry ->
                        !entry.getKey().equals("role") &&
                                !entry.getKey().equals("search"))
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> {
                            Filter filter = new Filter();
                            filter.setId(entry.getKey());
                            filter.setValue(value);
                            filter.setVariant("text");  // Default variant
                            filter.setOperator("iLike"); // Default operator
                            filter.setFilterId(UUID.randomUUID().toString()); // Generate unique ID
                            return filter;
                        })
                )
                .collect(Collectors.toList());
    }
}
