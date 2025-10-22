package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.entity.UserAddress;
import com.fastcampus.ecommerce.model.UserAddressRequest;
import com.fastcampus.ecommerce.model.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    UserAddressResponse createAddress(Long userId, UserAddressRequest request);
    List<UserAddressResponse> findByUserId(Long userId);
    UserAddressResponse findById(Long id);
    UserAddressResponse update(Long id, UserAddressRequest request);
    void delete(Long id);
    UserAddressResponse setDefaultAddress(Long userId, Long id);
}
