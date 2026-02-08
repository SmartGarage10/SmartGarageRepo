package com.example.demo.service;

import com.example.demo.DTO.PackDTO;
import com.example.demo.models.Pack;
import com.example.demo.models.User;

import java.util.List;

public interface PackService {
    List<Pack> getAllPacks();

    Pack createPack(User user, PackDTO packDTO);
    Pack update(User user, Long packId, PackDTO changes);

    void deletePack(User user, Long packId);
    void deletePacks(User user, List<Long> ids);
}
