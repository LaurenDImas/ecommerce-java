package com.fastcampus.ecommerce.repository;

import com.fastcampus.ecommerce.entity.Role;
import com.fastcampus.ecommerce.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    void deleteByIdUserId(Long userId);
}
