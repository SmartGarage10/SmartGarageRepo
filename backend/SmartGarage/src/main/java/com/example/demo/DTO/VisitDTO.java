package com.example.demo.DTO;

import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private double amount;
    private String currency;
    private Pack pack;
    private List<ServiceItemDTO> visitServices;
}
