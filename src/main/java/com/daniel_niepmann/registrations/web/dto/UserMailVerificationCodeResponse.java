package com.daniel_niepmann.registrations.web.dto;

import lombok.Builder;

@Builder
public record UserMailVerificationCodeResponse(String code) {
}
