//package com.example.demo.helpers;
//
//import com.example.demo.DTO.ServiceDTO;
//import com.example.demo.DTO.VisitDTO;
//import com.example.demo.models.*;
//import com.example.demo.service.ServiceService;
//import com.example.demo.service.UserService;
//import com.example.demo.service.VehicleService;
////import com.example.demo.service.ServiceItemService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.ArrayList;
//
//@Component
//public class VisitMapper {
//
//    private final UserService userService;
//    private final VehicleService vehicleService;
//    private final ServiceService serviceItemService;
//    private final ServiceMapper serviceMapper;
//
//    @Autowired
//    public VisitMapper(UserService userService, VehicleService vehicleService, ServiceService serviceItemService,  ServiceMapper serviceMapper) {
//        this.userService = userService;
//        this.vehicleService = vehicleService;
//        this.serviceItemService = serviceItemService;
//        this.serviceMapper = serviceMapper;
//    }
//
//    public Visit fromDto(int id, VisitDTO visitDto) {
//        Visit visit = fromDto(visitDto);
//        visit.setId(id);
//        return visit;
//    }
//
//    public Visit fromDto(VisitDTO visitDto) {
//        Visit visit = new Visit();
//        visit.setVisitDate(visitDto.getVisitDate());
//        visit.setStatus(visitDto.getStatus());
//        visit.setAmount(visitDto.getAmount());
//        visit.setCurrency(visitDto.getCurrency());
//
//        // Set employee
//        User employee = userService.getUserById(visitDto.getEmployee().getId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + visitDto.getEmployee().getId()));
//        visit.setEmployee(employee);
//
//        // Set vehicle (which has the client relationship)
//        Vehicle vehicle = vehicleService.getVehicleById(visitDto.getVehicle().getId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle ID: " + visitDto.getVehicle().getId()));
//        visit.setVehicle(vehicle);
//
//        // Set pack if provided
//        if (visitDto.getPack() != null) {
//            visit.setPack(visitDto.getPack());
//        }
//
//        // Handle services - three scenarios:
//        // 1. Regular pack from DB: pack has ID and is not CUSTOM PACK
//        // 2. CUSTOM PACK: pack name is "CUSTOM PACK"
//        // 3. No pack selected
//        List<Visit_Service> serviceOrderDetails = new ArrayList<>();
//
//        if (visitDto.getPack() != null) {
//            if (visitDto.getPack().getId() != 0 && !"CUSTOM PACK".equals(visitDto.getPack().getPackName())) {
//                // Scenario 1: Regular pack from DB - get services from the pack
//                Pack pack = visitDto.getPack();
//                if (pack.getServices() != null) {
//                    serviceOrderDetails = pack.getServices().stream()
//                            .map(service -> {
//                                Visit_Service detail = new Visit_Service();
//                                detail.setService(service);
//                                detail.setVisit(visit);
//                                return detail;
//                            })
//                            .collect(Collectors.toList());
//                }
//            } else if ("CUSTOM PACK".equals(visitDto.getPack().getPackName())) {
//                // Scenario 2: CUSTOM PACK - services come from the DTO
//                if (visitDto.getServices() != null && !visitDto.getServices().isEmpty()) {
//                    serviceOrderDetails = visitDto.getServices().stream()
//                            .map(serviceItem -> {
//                                // Try to find existing service by ID first, otherwise create new one
//                                ServiceItem service = serviceItemService.getServiceById(serviceItem.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid service ID: " + serviceItem.getId()));
//
//                                Visit_Service detail = new Visit_Service();
//                                detail.setService(service);
//                                detail.setVisit(visit);
//                                return detail;
//                            })
//                            .collect(Collectors.toList());
//                }
//            }
//        }
//        // Scenario 3: No pack selected - serviceOrderDetails remains empty
//
//        visit.setVisitServices(serviceOrderDetails);
//
//        return visit;
//    }
//
////    public VisitDTO toDto(Visit visit) {
////        VisitDTO visitDto = new VisitDTO();
////        // Client is accessed through vehicle.client
////        if (visit.getVehicle() != null && visit.getVehicle().getClient() != null) {
////            visitDto.setClient(visit.getVehicle().getClient());
////        }
////        visitDto.setVehicle(visit.getVehicle());
////        visitDto.setEmployee(visit.getEmployee());
////        visitDto.setVisitDate(visit.getVisitDate());
////        visitDto.setStatus(visit.getStatus());
////        visitDto.setAmount(visit.getAmount());
////        visitDto.setCurrency(visit.getCurrency());
////        visitDto.setPack(visit.getPack());
////
////        // Always return the services that are actually used in the visit
////        List<ServiceDTO> serviceDtos = new ArrayList<>();
////        if (visit.getVisitServices() != null && !visit.getVisitServices().isEmpty()) {
////            serviceDtos = visit.getVisitServices().stream()
////                    .map(detail -> {
////                        ServiceItem service = detail.getService();
////                        ServiceDTO serviceDto = new ServiceDTO();
////                        serviceDto.setId(service.getId());
////                        serviceDto.setServiceName(service.getServiceName());
////                        serviceDto.setPrice(service.getPrice());
////                        return serviceDto;
////                    })
////                    .collect(Collectors.toList());
////        }
////        visitDto.setServices(serviceDtos);
////
////        return visitDto;
////    }
//}