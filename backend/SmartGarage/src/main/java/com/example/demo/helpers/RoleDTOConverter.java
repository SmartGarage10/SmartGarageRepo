package com.example.demo.helpers;

import com.example.demo.DTO.RoleDTO;
import org.springframework.core.convert.converter.Converter;

public class RoleDTOConverter implements Converter<String, RoleDTO> {
    @Override
    public RoleDTO convert(String source) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName(source);
        return roleDTO;
    }
}
