package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.system.browser.nst.NstBrowserClient;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.GetProfilesResponse;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.embedded.Profile;
import com.daniel_niepmann.registrations.system.browser.nst.service.NstBrowserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationFacade {

    private final UserRegistrationService userRegistrationService;

    private final NstBrowserClient nstBrowserClient;

    private final NstBrowserService nstBrowserService;

    public void startUsersRegistration() {
        try {
            GetProfilesResponse profilesByCursor = nstBrowserClient.getProfilesByCursor();
            List<String> profileIds = profilesByCursor.data().profiles().stream().map(Profile::profileId).toList();
            if (profileIds.isEmpty()) throw new ApiException("No profiles in NST browser.", HttpStatus.NO_CONTENT.value());

            // inspect all profiles in progress and wait till execution up to 5 mins
            List<User> users = userRegistrationService.waitForUsersInProgress();

            List<String> limitedByUsersAvailable = profileIds.stream()
                    .limit(Math.min(users.size(), 10))
                    .toList();

            // start all profiles
            nstBrowserClient.startBrowsers(limitedByUsersAvailable);

            userRegistrationService.waitForUsersToCompleteRegistration(users.stream().map(User::getId).toList());

            // clear all profiles and stop
            nstBrowserClient.stopBrowsers(limitedByUsersAvailable);
            nstBrowserService.clearAllBrowsers(limitedByUsersAvailable);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
