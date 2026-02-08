package com.example.demo.repositories;

import com.example.demo.models.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long>, JpaSpecificationExecutor<Pack> {
    boolean existsPacksByPackName(String packName);
}
