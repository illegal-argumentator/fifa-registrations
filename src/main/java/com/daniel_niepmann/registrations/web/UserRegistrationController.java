package com.daniel_niepmann.registrations.web;

import com.daniel_niepmann.registrations.service.user.UserRegistrationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/registration")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserRegistrationFacade userRegistrationFacade;
    private Thread registrationThread;

    @PostMapping
    public void startUsersRegistration() {
        if (registrationThread != null && !registrationThread.isInterrupted()) {
            registrationThread.interrupt();
        }
        registrationThread = new Thread(userRegistrationFacade::processUsersRegistration, "Reg");
        registrationThread.start();
    }

    @DeleteMapping
    public void stopUsersRegistration() {
        if (registrationThread != null  && !registrationThread.isInterrupted()) {
            registrationThread.interrupt();
        }
    }
}
