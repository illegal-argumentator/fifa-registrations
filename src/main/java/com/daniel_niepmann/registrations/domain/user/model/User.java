package com.daniel_niepmann.registrations.domain.user.model;

import com.daniel_niepmann.registrations.domain.user.common.dto.Address;
import com.daniel_niepmann.registrations.domain.user.common.dto.Bio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id;

    private String email;

    private String domain;

    private String password;

    private String phoneNumber;

    @Embedded
    private Address address;

    @Embedded
    private Bio bio;

    private boolean isUsed;

    private String errorMessage;

    private LocalDate registeredAt;
}
