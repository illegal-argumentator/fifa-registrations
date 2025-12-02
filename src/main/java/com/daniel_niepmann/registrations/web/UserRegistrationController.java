package com.daniel_niepmann.registrations.web;

import com.daniel_niepmann.registrations.service.user.UserRegistrationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserRegistrationFacade userRegistrationFacade;

    @PostMapping("/users")
    public void startUsersRegistration() {
        userRegistrationFacade.startUsersRegistration();
    }

}
