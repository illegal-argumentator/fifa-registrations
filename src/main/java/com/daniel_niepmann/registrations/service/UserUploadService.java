package com.daniel_niepmann.registrations.service;

import com.daniel_niepmann.registrations.domain.user.common.dto.Address;
import com.daniel_niepmann.registrations.domain.user.common.dto.Bio;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
// TODO refactor, add mapper strategy, handle exception
public class UserUploadService {

    private final UserService userService;

    @Transactional
    public void uploadUsers(MultipartFile file) {
        Set<User> users = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean skipHeader = true;

            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                User user = User.builder()
                        .id(Long.valueOf(parts[0]))
                        .email(parts[5])
                        .domain(parts[4])
                        .password(parts[12])
                        .phoneNumber(parts[13])
                        .address(Address.builder()
                                .city(parts[1])
                                .countryCode(parts[2])
                                .houseNumber(parts[8])
                                .postcode(parts[15])
                                .state(parts[18])
                                .street(parts[19])
                                .build())
                        .bio(Bio.builder()
                                .firstName(parts[7])
                                .lastName(parts[11])
                                .dateOfBirth(LocalDate.parse(parts[3]))
                                .pii(parts[14])
                                .sex(parts[17])
                                .build())
                        .build();

                users.add(user);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userService.saveAll(users);
    }
}
