package com.daniel_niepmann.registrations.system.browser.nst.service;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.system.browser.nst.NstBrowserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NstBrowserService {

    private final NstBrowserClient nstBrowserClient;

    public void killAllBrowsers(List<String> profileIds) {
        try {
            nstBrowserClient.stopBrowsers(profileIds);
        } catch (ApiException e) {
            log.warn("Probably no profile: {}", e.getMessage());
        }
    }

}
