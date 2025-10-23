package com.fastcampus.ecommerce.controller;

import com.fastcampus.ecommerce.common.SecurityUtils;
import com.fastcampus.ecommerce.model.UserAddressRequest;
import com.fastcampus.ecommerce.model.UserAddressResponse;
import com.fastcampus.ecommerce.service.UserAddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/address")
@SecurityRequirement(name = "Bearer")
public class AddressController {
    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<UserAddressResponse> create(@Valid @RequestBody UserAddressRequest userAddressRequest) {
        UserAddressResponse response = userAddressService.createAddress(
                SecurityUtils.getCurrentUser().getUser().getUserId(), userAddressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> findAddressByUserId() {
        List<UserAddressResponse> addressResponses = userAddressService.findByUserId(
                SecurityUtils.getCurrentUser().getUser().getUserId());
        return ResponseEntity.ok(addressResponses);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> get(@PathVariable Long addressId) {
        UserAddressResponse response =  userAddressService.findById(addressId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> update(@PathVariable Long addressId,
                                                      @Valid @RequestBody UserAddressRequest userAddressRequest) {
        UserAddressResponse response = userAddressService.update(addressId, userAddressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> delete(@PathVariable Long addressId) {
        userAddressService.delete(addressId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<UserAddressResponse> setDefaultAddress(@PathVariable Long addressId) {
        UserAddressResponse response = userAddressService.setDefaultAddress(
                SecurityUtils.getCurrentUser().getUser().getUserId(), addressId);

        return ResponseEntity.ok(response);
    }
}
