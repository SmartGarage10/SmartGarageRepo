package com.example.demo.repositories;

import com.example.demo.models.Pack;
import com.example.demo.models.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long>, JpaSpecificationExecutor<Pack> {
    boolean existsPacksByPackName(String packName);

    List<Pack> findAllByServicesContaining(ServiceItem service);
}
