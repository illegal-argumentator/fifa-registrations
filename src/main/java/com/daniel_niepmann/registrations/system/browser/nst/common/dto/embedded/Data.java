package com.daniel_niepmann.registrations.system.browser.nst.common.dto.embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Data(
        @JsonProperty("docs")
        List<Profile> profiles
) {
}
