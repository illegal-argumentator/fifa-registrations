package com.daniel_niepmann.registrations.web;

import com.daniel_niepmann.registrations.domain.user.service.UserService;
import com.daniel_niepmann.registrations.web.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.daniel_niepmann.registrations.common.mapper.UserMapper.mapUpdateUserRequestToUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PutMapping
    public void update(@RequestParam Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        userService.update(id, mapUpdateUserRequestToUser(updateUserRequest));
    }

}
