package com.daniel_niepmann.registrations.system.browser.nst.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateProfileRequest {
    private String name;
    private String platform;
    private String proxy;

    @Data
    public static class Fingerprint {
        private Localization localization;

        @Data
        public static class Localization {
            private String language;
            private List<String> languages;
            private String timezone;
        }
    }
}


