package com.daniel_niepmann.registrations.domain.user.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bio {

    private String pii;

    private String sex;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

}
