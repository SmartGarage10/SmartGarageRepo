package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private String id;          // Field name (e.g., "email")
    private Object value;       // Filter value (e.g., "eva@example.com")
    private String variant;     // UI variant (e.g., "text", "select")
    private String operator;    // Operator (e.g., "iLike", "equals")

    @JsonProperty("filterId")
    private String filterId;    // Frontend-generated filter ID

}
