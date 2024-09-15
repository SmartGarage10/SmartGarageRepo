package com.example.demo.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

@Data
@NoArgsConstructor
public class UserDTO extends PasswordDTO {
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
    private RoleDTO role;
}
