package com.airgear.user.account.dto;

import com.airgear.model.Role;
import com.airgear.model.User;
import com.airgear.model.UserStatus;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Set;

public record UserResponse(Long id,
                           String email,
                           String phone,
                           String name,
                           UserStatus status,
                           OffsetDateTime createdAt,
                           OffsetDateTime deleteAt,
                           OffsetDateTime lastActivity,
                           Float rating,
                           Set<Role> roles) {

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                user.getRating(),
                EnumSet.copyOf(user.getRoles()));
    }

    public static UserResponse fromUserWithBasicAttributes(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getDeleteAt(),
                user.getLastActivity(),
                user.getRating(),
                null);
    }
}
