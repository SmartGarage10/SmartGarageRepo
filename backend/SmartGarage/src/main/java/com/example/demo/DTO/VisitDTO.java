package com.example.demo.DTO;

import com.example.demo.models.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDTO {
    private User client;
    private Vehicle vehicle;
    private User employee;
    private LocalDateTime visitDate;
    private String status;
    private BigDecimal amount;
    private List<VisitItem> visitItems;
}
