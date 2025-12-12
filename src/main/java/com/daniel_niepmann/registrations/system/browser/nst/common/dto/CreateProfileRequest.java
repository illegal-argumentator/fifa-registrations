package com.daniel_niepmann.registrations.system.browser.nst.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateProfileRequest {
    private String name;
    private String platform;
    private String groupId;
    private String proxy;
    private Fingerprint fingerprint;

    @Data
    @AllArgsConstructor
    public static class Fingerprint {
        private Screen screen;

        @Data
        @AllArgsConstructor
        public static class Screen {
            private int width;
            private int height;
        }
    }
}


