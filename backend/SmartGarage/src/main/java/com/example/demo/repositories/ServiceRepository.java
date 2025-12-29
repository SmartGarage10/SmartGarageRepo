package com.example.demo.repositories;

import com.example.demo.models.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, Long>, JpaSpecificationExecutor<ServiceItem> {
    Optional<ServiceItem> findByServiceName(String serviceName);
    Optional<ServiceItem> findById(Long id);

    boolean existsByServiceName(String serviceName);
}
