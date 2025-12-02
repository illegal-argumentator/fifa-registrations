package com.daniel_niepmann.registrations.system.browser.nst.service;

import com.daniel_niepmann.registrations.system.browser.nst.NstBrowserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NstBrowserService {

    private final NstBrowserClient nstBrowserClient;

    public void clearAllBrowsers(List<String> profileIds) {
        for (String profileId : profileIds) {
            nstBrowserClient.clearProfileCookies(profileId);
            nstBrowserClient.clearProfileCache(profileId);
        };
    }

}
