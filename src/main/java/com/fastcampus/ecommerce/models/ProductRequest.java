package com.fastcampus.ecommerce.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Nama produk tidak boleh kosong")
    @Size(min = 3, max = 20, message = "Nama produk harus antara 3 hingga 20 karakter")
    String name;

    @NotNull(message = "Harga tidak boleh kosong")
    @Positive(message = "Harga harus lebih besar dari 0")
    @Digits(integer = 10, fraction = 2, message = "Harga harus memiliki maksimal 10 digit dan 2 desimal")
    BigDecimal price;

    @NotNull(message = "Deskripsi tidak boleh kosong")
    @Size(max= 1000, message = "Deskripsi maksimal 1000 karakter")
    String description;
}
