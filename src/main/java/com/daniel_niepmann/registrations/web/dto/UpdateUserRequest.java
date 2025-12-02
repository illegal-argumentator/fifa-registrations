package com.daniel_niepmann.registrations.web.dto;

import com.daniel_niepmann.registrations.domain.user.common.type.Status;

public record UpdateUserRequest(
        String errorMessage,
        Status status) {
}
