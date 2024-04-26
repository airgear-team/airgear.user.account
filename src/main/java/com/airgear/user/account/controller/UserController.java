package com.airgear.user.account.controller;

import com.airgear.user.account.dto.UserChangePasswordRequest;
import com.airgear.user.account.dto.UserMergeRequest;
import com.airgear.user.account.dto.UserResponse;
import com.airgear.user.account.exception.UserExceptions;
import com.airgear.user.account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.airgear.user.account.utils.Routes.USERS;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(USERS)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(
            value = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse getCurrentUser(@AuthenticationPrincipal String email) {
        return userService.findByEmail(email)
                .orElseThrow(() -> UserExceptions.userNotFound(email));
    }

    @PatchMapping(
            value = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse mergeCurrentUser(@AuthenticationPrincipal String email,
                                         @RequestBody @Valid UserMergeRequest request) {
        return userService.mergeByEmail(email, request);
    }

    @PatchMapping(
            value = "/me/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserResponse changeCurrentUserPassword(@AuthenticationPrincipal String email,
                                                  @RequestBody @Valid UserChangePasswordRequest request) {
        return userService.changePasswordByEmail(email, request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(@AuthenticationPrincipal String email) {
        userService.deleteByEmail(email);
    }
}
