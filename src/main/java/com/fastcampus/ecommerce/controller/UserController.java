package com.fastcampus.ecommerce.controller;

import com.fastcampus.ecommerce.common.errors.ForbiddenAccessException;
import com.fastcampus.ecommerce.model.UserInfo;
import com.fastcampus.ecommerce.model.UserResponse;
import com.fastcampus.ecommerce.model.UserUpdateRequest;
import com.fastcampus.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        UserResponse userResponse = UserResponse.fromUserAndRoles(userInfo.getUser(), userInfo.getRoles());

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        // login saya sendiri atau admin boleh mengupdate user
        if (!userInfo.getUser().getUserId().equals(id) && userInfo.getAuthorities().stream()
                .noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            throw new ForbiddenAccessException("You do not have permission to update this user.");
        }

        UserResponse updateUser = userService.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (!userInfo.getUser().getUserId().equals(id) && !userInfo.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new ForbiddenAccessException("You do not have permission to update this user.");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
