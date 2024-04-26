package com.airgear.user.account.service.impl;

import com.airgear.model.CustomUserDetails;
import com.airgear.model.User;
import com.airgear.user.account.dto.UserChangePasswordRequest;
import com.airgear.user.account.dto.UserMergeRequest;
import com.airgear.user.account.dto.UserResponse;
import com.airgear.user.account.exception.UserExceptions;
import com.airgear.user.account.repository.UserRepository;
import com.airgear.user.account.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '" + email + "' not found"));

        return new CustomUserDetails(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserResponse::fromUser);
    }

    @Override
    public UserResponse mergeByEmail(String email, UserMergeRequest request) {
        User user = getUser(email);
        return UserResponse.fromUser(merge(user, request));
    }

    @Override
    public UserResponse changePasswordByEmail(String email, UserChangePasswordRequest request) {
        User user = getUser(email);
        changePassword(user, request.oldPassword(), request.newPassword());
        return UserResponse.fromUser(user);
    }

    @Override
    public void deleteByEmail(String email) {
        if (!userRepository.existsByEmail(email)) throw UserExceptions.userNotFound(email);
        userRepository.deleteByEmail(email);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> UserExceptions.userNotFound(email));
    }

    private User merge(User user, UserMergeRequest request) {
        String email = request.email();
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) throw UserExceptions.duplicateEmail(email);
            user.setEmail(email);
        }
        String phone = request.phone();
        if (phone != null && !phone.equals(user.getPhone())) {
            if (userRepository.existsByPhone(phone)) throw UserExceptions.duplicatePhone(phone);
            user.setPhone(phone);
        }
        String name = request.name();
        if (name != null && !name.equals(user.getName())) {
            user.setName(name);
        }
        return user;
    }

    private void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw UserExceptions.wrongPassword();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
