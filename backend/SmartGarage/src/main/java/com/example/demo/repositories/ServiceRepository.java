package com.example.demo.repositories;

import com.example.demo.models.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceItem, Integer>, JpaSpecificationExecutor<ServiceItem> {
    Optional<ServiceItem> findByServiceName(String serviceName);
    Optional<ServiceItem> findById(Integer id);

    boolean existsByServiceName(String serviceName);
}
