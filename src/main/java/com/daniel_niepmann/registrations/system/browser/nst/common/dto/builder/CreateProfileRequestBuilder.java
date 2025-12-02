package com.daniel_niepmann.registrations.system.browser.nst.common.dto.builder;

import com.daniel_niepmann.registrations.domain.proxy.model.Proxy;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.CreateProfileRequest;

import java.util.List;

import static com.daniel_niepmann.registrations.common.utils.LocationUtils.EN_AMERICAN_LANGUAGE_CODE;
import static com.daniel_niepmann.registrations.common.utils.LocationUtils.EN_LANGUAGE_CODE;

public class CreateProfileRequestBuilder {
    
    private static final String WINDOWS_PLATFORM = "Windows";
    
    private static final String PROXY_URL_TEMPLATE = "http://%s:%s@%s:%d";

    public static CreateProfileRequest buildCreateProfileRequest(String profileName, Proxy proxy) {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName(profileName);
        request.setPlatform(WINDOWS_PLATFORM);
        request.setProxy(
                PROXY_URL_TEMPLATE.formatted(
                        proxy.getUsername(),
                        proxy.getPassword(),
                        proxy.getHost(),
                        proxy.getPort()
                )
        );

        CreateProfileRequest.Fingerprint fingerprint = new CreateProfileRequest.Fingerprint();

        CreateProfileRequest.Fingerprint.Localization localization = new CreateProfileRequest.Fingerprint.Localization();
        localization.setLanguage(EN_AMERICAN_LANGUAGE_CODE);
        localization.setLanguages(List.of(EN_AMERICAN_LANGUAGE_CODE, EN_LANGUAGE_CODE));
        fingerprint.setLocalization(localization);

        return request;
    }
}
