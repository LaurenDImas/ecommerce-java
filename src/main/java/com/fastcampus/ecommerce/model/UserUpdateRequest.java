package com.fastcampus.ecommerce.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.class)
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Username harus antara 3 hingga 50 karakter")
    private String username;

    @Email(message = "Format email tidak valid")
    private String email;

    @Size(min = 8, max = 100, message = "Password harus antara")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password harus mengandung minimal satu huruf, satu angka, dan satu karakter khusus")
    private String newPassword;
    private String currentPassword;
}
