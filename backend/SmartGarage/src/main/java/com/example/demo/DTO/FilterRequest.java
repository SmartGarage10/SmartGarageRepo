package com.example.demo.DTO;

import lombok.Data;

import java.util.List;

@Data
public class FilterRequest {
    private List<Filter> filters;

}
