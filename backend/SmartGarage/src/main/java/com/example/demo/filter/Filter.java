package com.example.demo.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private String id;
    private Object value;
    private String variant;
    private String operator;

    @JsonProperty("filterId")
    private String filterId;

}
