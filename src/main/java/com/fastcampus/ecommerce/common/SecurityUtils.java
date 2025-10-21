package com.fastcampus.ecommerce.common;


import com.fastcampus.ecommerce.common.errors.ForbiddenAccessException;
import com.fastcampus.ecommerce.model.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {
    public static Optional<UserInfo> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        return Optional.ofNullable(userInfo);
    }

    public static UserInfo getCurrentUser() {
        return getAuthentication()
                .orElseThrow(() -> new ForbiddenAccessException("No authenticated user in SecurityContext"));
    }

}
