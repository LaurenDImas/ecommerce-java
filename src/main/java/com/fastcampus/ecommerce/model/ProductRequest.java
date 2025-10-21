package com.fastcampus.ecommerce.model;

import com.fastcampus.ecommerce.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Nama produk tidak boleh kosong")
    @Size(min = 3, max = 20, message = "Nama produk harus antara 3 hingga 20 karakter")
    private String name;

    @NotNull(message = "Harga tidak boleh kosong")
    @Positive(message = "Harga harus lebih besar dari 0")
    @Digits(integer = 10, fraction = 2, message = "Harga harus memiliki maksimal 10 digit dan 2 desimal")
    private BigDecimal price;

    @NotNull(message = "Deskripsi tidak boleh kosong")
    @Size(max= 1000, message = "Deskripsi maksimal 1000 karakter")
    private String description;

    @NotNull(message = "Kuantitas stok tidak boleh kosong")
    @Min(value = 0, message = "Kuantitas stok tidak boleh negatif")
    private Integer stockQuantity;

    @NotNull(message = "Berat tidak boleh kosong")
    @Min(value = 1000, message = "Berat minimal adalah 1000 gram")
    private BigDecimal weight;

    @NotEmpty(message = "Kategori tidak boleh kosong")
    private List<Long> categoryIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // akan diabaikan saat request masuk
    private User user;
}
