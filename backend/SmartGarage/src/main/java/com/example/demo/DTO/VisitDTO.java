package com.example.demo.DTO;

import com.example.demo.models.Pack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitDTO {
    private int visitId;
    private int vehicleId;
    private int employeeId;
    private LocalDateTime visitDate;
    private String status;
    private double amount;
    private String currency;
    private Pack pack;  // enum from Visit
    private List<ServiceDTO> services;
}
