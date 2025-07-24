package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private int serviceId;
    private String serviceName;
    private String serviceDescription;
    private double servicePrice;
}
