package com.daniel_niepmann.registrations.web;

import com.daniel_niepmann.registrations.service.UserUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users-upload")
@RequiredArgsConstructor
public class UserUploadController {

    private final UserUploadService userUploadService;

    @PostMapping("/csv")
    public void uploadCsv(@RequestParam MultipartFile file) {
        userUploadService.uploadUsers(file);
    }

}
