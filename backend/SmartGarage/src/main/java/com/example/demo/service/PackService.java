package com.example.demo.service;

import com.example.demo.models.Pack;
import com.example.demo.models.User;
import com.example.demo.models.Vehicle;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface PackService {
    List<Pack> getAllPacks();
    Optional<Pack> getPackById(int packId);

    Pack createPack(User user, Pack pack);
    Pack update(User user, int packId, Pack changes);

    void deletePack(User user, int packId);
    void deletePacks(User user, List<Integer> ids);
}
