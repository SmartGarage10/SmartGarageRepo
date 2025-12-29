package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private String roleName;

    @JsonCreator
    public static RoleDTO fromString(String roleName) {
        return RoleDTO.builder()
                .roleName(roleName)
                .build();
    }
}