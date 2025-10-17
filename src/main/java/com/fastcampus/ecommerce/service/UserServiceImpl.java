package com.fastcampus.ecommerce.service;

import com.fastcampus.ecommerce.common.errors.BadRequestException;
import com.fastcampus.ecommerce.common.errors.UserNotFoundException;
import com.fastcampus.ecommerce.common.errors.UsernameAlreadyExistsException;
import com.fastcampus.ecommerce.entity.Role;
import com.fastcampus.ecommerce.entity.User;
import com.fastcampus.ecommerce.entity.UserRole;
import com.fastcampus.ecommerce.model.UserRegisterRequest;
import com.fastcampus.ecommerce.model.UserResponse;
import com.fastcampus.ecommerce.model.UserUpdateRequest;
import com.fastcampus.ecommerce.repository.RoleRepository;
import com.fastcampus.ecommerce.repository.UserRepository;
import com.fastcampus.ecommerce.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest registerRequest) {
        if (existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already taken : " + registerRequest.getUsername());
        }
        if (existsByEmail(registerRequest.getEmail())) {
            throw new UsernameAlreadyExistsException("Email already taken : " + registerRequest.getEmail());
        }
        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirmation())){
            throw new BadRequestException("Password and password confirmation do not match");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .enabled(true)
                .password(encodedPassword)
                .build();

        userRepository.save(user);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BadRequestException("Default role not found"));

        UserRole userRoleRelation = UserRole.builder()
                .id(new UserRole.UserRoleId(user.getUserId(), userRole.getRoleId()))
                .build();
        userRoleRepository.save(userRoleRelation);

        return UserResponse.fromUserAndRoles(user, List.of(userRole));
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        List<Role> roles = roleRepository.findByUserId(id);
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    public UserResponse findByKeyword(String keyword) {
        User user = userRepository.findByKeyword(keyword)
                .orElseThrow(() -> new UserNotFoundException("User not found with username / email: " + keyword));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if(!passwordEncoder.matches(request.getCurrentPassword(), request.getNewPassword())){
                throw new BadRequestException("Password and new password do not match");
            }

            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already taken : " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new UsernameAlreadyExistsException("Email already taken : " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        userRepository.save(user);
        List<Role> roles = roleRepository.findByUserId(id);
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRoleRepository.deleteByUserId(id);
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
