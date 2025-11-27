package com.daniel_niepmann.registrations.domain.user.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Sex {

    MALE("male"),
    FEMALE("female");

    private final String sex;
}
