package com.daniel_niepmann.registrations.system.browser.nst.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProfileResponse {

    private CreateProfileResponseData data;

    private boolean err;

    private String msg;

    private int code;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateProfileResponseData {
        private String profileId;

        private String fingerprintId;

        private String groupId;

        private String teamId;

        private String userId;

        private String name;

        private int kernel;

        private String kernelVersion;

        private String kernelMilestone;

        private String uaFullVersion;

        private int platform;

        private String platformVersion;

        private boolean saveLocal;

        private int status;

        private String note;

        private Object tags;

        private String _id;
    }
}
