package com.example.demo.DTO;

import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackDTO {
    private String packName;      // BASIC, STANDARD, PREMIUM, CUSTOM
    private String description;
    private BigDecimal amount;
    private List<ServiceItem> services;
}
