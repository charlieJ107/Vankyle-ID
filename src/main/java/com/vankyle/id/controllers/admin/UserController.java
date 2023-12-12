package com.vankyle.id.controllers.admin;

import com.vankyle.id.models.admin.user.UserItem;
import com.vankyle.id.models.admin.user.UserItemResponse;
import com.vankyle.id.models.admin.user.UserListItem;
import com.vankyle.id.models.admin.user.UserListResponse;
import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import com.vankyle.id.service.security.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${vankyle.id.api-path}/admin/users")
public class UserController {
    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping("/")
    public UserListResponse getUsers() {
        var response = new UserListResponse();
        response.setStatus(2000);
        response.setUsers(userManager.findAllUsers().stream().map(user -> {
            var item = new UserListItem();
            item.setId(user.getId());
            item.setUsername(user.getUsername());
            item.setEmail(user.getEmail());
            item.setName(user.getName());
            return item;
        }).toList());
        return response;
    }

    @GetMapping("/{id}")
    public UserItemResponse getUser(@PathVariable String id) {
        var user = userManager.findById(id);
        if (user == null) {
            var response = new UserItemResponse();
            response.setStatus(4004);
            return response;
        }
        user.eraseCredentials();
        var response = new UserItemResponse();
        response.setStatus(2000);
        response.setUser(new UserItem(user));
        return response;
    }

    @PostMapping("/")
    public UserItemResponse createUser(@RequestBody UserItem userItem) {
        if (!StringUtils.hasText(userItem.getUsername())) {
            var response = new UserItemResponse();
            response.setStatus(4000);
            response.setMessage("Username is required");
            return response;
        }
        if (!StringUtils.hasText(userItem.getPassword())) {
            var response = new UserItemResponse();
            response.setStatus(4000);
            response.setMessage("Password is required");
            return response;
        }
        List<GrantedAuthority> authorities = new java.util.ArrayList<>(
                userItem.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList());
        userItem.getRoles().stream().filter(StringUtils::hasText).forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        var user = User.withUsername(userItem.getUsername())
                .id(userItem.getId())
                .rawPassword(userItem.getPassword())
                .accountExpired(userItem.isAccountExpired())
                .accountLocked(userItem.isAccountLocked())
                .credentialsExpired(userItem.isCredentialsExpired())
                .disabled(!userItem.isEnabled())
                .authorities(authorities)
                .email(userItem.getEmail())
                .emailVerified(userItem.isEmailVerified())
                .phone(userItem.getPhone())
                .phoneVerified(userItem.isPhoneVerified())
                .name(userItem.getName())
                .build();
        try {
            userManager.createUser(user);
        } catch (UsernameAlreadyExistsException e) {
            var response = new UserItemResponse();
            response.setStatus(4001);
            response.setMessage(e.getMessage());
            return response;
        }
        var response = new UserItemResponse();
        response.setStatus(2000);
        user.eraseCredentials();
        response.setUser(new UserItem(user));
        return response;
    }

    @PutMapping("/{id}")
    public UserItemResponse updateUser(@PathVariable String id, @RequestBody UserItem userItem) {
        var user = userManager.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Set<GrantedAuthority> authorities = userItem.getAuthorities().stream()
                .filter(StringUtils::hasText)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        userItem.getRoles().stream().filter(StringUtils::hasText)
                .forEach(role -> authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role)));
        user.setUsername(userItem.getUsername());
        user.setAccountNonExpired(!userItem.isAccountExpired());
        user.setAccountNonLocked(!userItem.isAccountLocked());
        user.setCredentialsNonExpired(!userItem.isCredentialsExpired());
        user.setEnabled(userItem.isEnabled());
        user.setMfaEnabled(userItem.isMfaEnabled());
        user.setName(userItem.getName());
        user.setEmail(userItem.getEmail());
        user.setEmailVerified(userItem.isEmailVerified());
        user.setPhone(userItem.getPhone());
        user.setPhoneVerified(userItem.isPhoneVerified());
        user.setPicture(userItem.getPicture());
        user.setAuthorities(authorities);

        try {
            userManager.updateUser(user);
            if (StringUtils.hasText(userItem.getPassword())) {
                userManager.resetPassword(user, userItem.getPassword());
            }
        } catch (UsernameNotFoundException e) {
            var response = new UserItemResponse();
            response.setStatus(4004);
            response.setMessage(e.getMessage());
            return response;
        }
        var response = new UserItemResponse();
        response.setStatus(2000);
        user.eraseCredentials();
        response.setUser(new UserItem(user));
        return response;
    }
}
