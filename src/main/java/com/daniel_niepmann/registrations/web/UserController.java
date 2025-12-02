package com.daniel_niepmann.registrations.web;

import com.daniel_niepmann.registrations.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

//    @PutMapping
//    public void update(@RequestParam String id, @RequestBody ) {
//        userService.update(id);
//    }

}
