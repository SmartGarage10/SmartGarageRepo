package com.example.demo.helpers;

import com.example.demo.DTO.ServiceDTO;
import com.example.demo.DTO.VisitDTO;
import com.example.demo.models.*;
import com.example.demo.service.UserService;
import com.example.demo.service.VehicleService;
import com.example.demo.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class VisitMapper {

    private final UserService userService;
    private final VehicleService vehicleService;

    @Autowired
    public VisitMapper(UserService userService, VehicleService vehicleService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    public Visit fromDto(int id, VisitDTO visitDto) {
        Visit visit = fromDto(visitDto);
        visit.setVisitId(id);
        return visit;
    }

    public Visit fromDto(VisitDTO visitDto) {
        Visit visit = new Visit();
        visit.setVisitDate(visitDto.getVisitDate());

        User employee = userService.getUserById(visitDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + visitDto.getEmployeeId()));
        visit.setEmployee(employee);

        Vehicle vehicle = vehicleService.getVehicleById(visitDto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + visitDto.getVehicleId()));
        visit.setVehicle(vehicle);

        // Map ServiceDTO to ServiceOrderDetails
        List<ServiceOrderDetails> serviceOrderDetails = visitDto.getServices().stream()
                .map(serviceDto -> {
                    ServiceItem service = new ServiceItem();
                    service.setServiceId(serviceDto.getServiceId());
                    service.setServiceName(serviceDto.getServiceName());
                    service.setPrice(serviceDto.getServicePrice());

                    ServiceOrderDetails detail = new ServiceOrderDetails();
                    detail.setService(service);
                    detail.setVisit(visit);  // Set the visit here
                    return detail;
                })
                .collect(Collectors.toList());

        visit.setServiceOrderDetails(serviceOrderDetails);

        return visit;
    }

    public VisitDTO toDto(Visit visit) {
        VisitDTO visitDto = new VisitDTO();
        visitDto.setVisitId(visit.getVisitId());
        visitDto.setVisitDate(visit.getVisitDate());
        visitDto.setEmployeeId(visit.getEmployee().getId());
        visitDto.setVehicleId(visit.getVehicle().getVehicleId());

        List<ServiceDTO> serviceDtos = visit.getServiceOrderDetails().stream()
                .map(detail -> {
                    ServiceItem service = detail.getService();
                    return new ServiceDTO(
                            service.getServiceId(),
                            service.getServiceName(),
                            service.getServiceDescription(),
                            service.getPrice()
                    );
                })
                .collect(Collectors.toList());

        visitDto.setServices(serviceDtos);

        return visitDto;
    }
}