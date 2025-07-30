package com.example.demo.DTO;

import com.example.demo.helpers.RoleDTODeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(using = RoleDTODeserializer.class) // Add this annotation
public class RoleDTO {
    private int roleId;
    private String roleName;

    // Constructor for string input
    @JsonCreator
    public RoleDTO(String roleName) {
        this.roleName = roleName;
    }

    // Constructor for full object
    @JsonCreator
    public RoleDTO(
            @JsonProperty("roleId") int roleId,
            @JsonProperty("roleName") String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
}