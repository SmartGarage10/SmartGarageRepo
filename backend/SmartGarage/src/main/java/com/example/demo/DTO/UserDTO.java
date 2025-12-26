package com.example.demo.DTO;

import com.example.demo.models.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull
    @Size(min = 5, max = 50, message = "The name should be between 5 and 50 symbols")
    private String name;

    @NotNull
    @Size(min = 2, max = 20, message = "Username should be between 2 and 20 symbols")
    private String username;

    @Unique
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Unique
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotNull
    @Size(min = 10, max = 40, message = "Address should be between 10 and 40 symbols")
    private String address;

    @NotNull
    private RoleDTO role;
}
