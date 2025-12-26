package com.example.demo.DTO;


import com.example.demo.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  RoleDTO {
    private Long roleId;
    private String roleName;
}