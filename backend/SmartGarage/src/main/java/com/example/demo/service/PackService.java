package com.example.demo.service;

import com.example.demo.DTO.pack.PackCreateDTO;
import com.example.demo.DTO.pack.PackResponseDTO;
import com.example.demo.DTO.pack.PackUpdateDTO;
import com.example.demo.models.User;

import java.util.List;

public interface PackService {
    List<PackResponseDTO> getAllPacks();

    PackResponseDTO createPack(PackCreateDTO packDTO);
    PackResponseDTO update(Long packId, PackUpdateDTO changes);

    void deletePack(User user, Long packId);
    void deletePacks(User user, List<Long> ids);
}
