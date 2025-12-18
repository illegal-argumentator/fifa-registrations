package com.daniel_niepmann.registrations.web;

import com.daniel_niepmann.registrations.service.user.UserMailVerificationService;
import com.daniel_niepmann.registrations.web.dto.UserMailVerificationCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/mail-verification")
@RequiredArgsConstructor
public class UserMailVerificationController {

    private final UserMailVerificationService userMailVerificationService;

    @GetMapping
    public ResponseEntity<UserMailVerificationCodeResponse> verifyUserMail(@RequestParam Long id) {
        return ResponseEntity.ok(userMailVerificationService.verifyUserMail(id));
    }

    @GetMapping
    public ResponseEntity<UserMailVerificationCodeResponse> verifyUserMail(@RequestParam String email) {
        return ResponseEntity.ok(userMailVerificationService.verifyUserMail(email));
    }
}
