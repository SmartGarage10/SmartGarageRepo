package com.example.demo.repositories;

import com.example.demo.models.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceItem, Integer> {
    List<ServiceItem> findByServiceNameContaining(String serviceName);
    List<ServiceItem> findByPriceBetween(double minPrice, double maxPrice);
}
