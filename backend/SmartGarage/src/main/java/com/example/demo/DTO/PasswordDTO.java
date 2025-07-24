package com.example.demo.DTO;

import com.example.demo.validators.ValidPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordDTO {
    @NotNull
    @Size(min = 4,
            message = "Password should be at least 4 symbols")
    @ValidPassword
    private String password;
}
