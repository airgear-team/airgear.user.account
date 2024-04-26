package com.airgear.user.account.service;

import com.airgear.user.account.dto.UserChangePasswordRequest;
import com.airgear.user.account.dto.UserMergeRequest;
import com.airgear.user.account.dto.UserResponse;

import java.util.Optional;

public interface UserService {

    Optional<UserResponse> findByEmail(String email);

    UserResponse mergeByEmail(String email, UserMergeRequest request);

    UserResponse changePasswordByEmail(String email, UserChangePasswordRequest request);

    void deleteByEmail(String email);

}
