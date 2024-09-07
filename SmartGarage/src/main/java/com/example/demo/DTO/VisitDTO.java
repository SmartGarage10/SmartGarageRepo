package com.example.demo.DTO;

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
    private int employeeId;
    private int vehicleId;
    private LocalDateTime visitDate;
    private List<ServiceDTO> services;
}
