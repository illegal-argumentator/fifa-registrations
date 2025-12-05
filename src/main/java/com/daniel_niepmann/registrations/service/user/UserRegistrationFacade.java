package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import com.daniel_niepmann.registrations.system.browser.nst.NstBrowserClient;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.GetProfilesResponse;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.embedded.Profile;
import com.daniel_niepmann.registrations.system.browser.nst.service.NstBrowserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.daniel_niepmann.registrations.common.utils.ProxyUtils.rotateProxyByUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationFacade {

    private final UserRegistrationService userRegistrationService;

    private final NstBrowserClient nstBrowserClient;

    private final NstBrowserService nstBrowserService;

    private final UserService userService;

    public void startUserRegistration() {
            GetProfilesResponse profilesByCursor = nstBrowserClient.getProfilesByCursor();
            List<String> profileIds = profilesByCursor.data().profiles().stream().map(Profile::profileId).toList();

            if (profileIds.isEmpty()) throw new ApiException("No profiles in NST browser.", HttpStatus.NO_CONTENT.value());

            List<String> limitedByUsersAvailable = profileIds.stream().limit(10).toList();
        try {
            nstBrowserService.clearAllBrowsers(limitedByUsersAvailable);
            nstBrowserClient.startBrowsers(limitedByUsersAvailable);
            List<User> users = userRegistrationService.waitForUsersInProgress();

            userRegistrationService.waitForUsersToCompleteRegistration(users.stream().map(User::getId).toList());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            userService.failAllUsersInProgress();
            throw exception;
        } finally {
            nstBrowserClient.stopBrowsers(limitedByUsersAvailable);
            nstBrowserService.clearAllBrowsers(limitedByUsersAvailable);
        }
    }

    public void processUsersRegistration() {
        List<String> rotateUrls = List.of(
                "https://reboot.connect.resocks.net/change-ip?uuid=JKyMqgXEWf",
                "https://reboot.connect.resocks.net/change-ip?uuid=8auIqqHRCr",
                "https://reboot.connect.resocks.net/change-ip?uuid=e8QyvytL6G",
                "https://reboot.connect.resocks.net/change-ip?uuid=v6ZcH1QTED",
                "https://reboot.connect.resocks.net/change-ip?uuid=WCB7nI6F3V",
                "https://reboot.connect.resocks.net/change-ip?uuid=NxJDluZCcb",
                "https://reboot.connect.resocks.net/change-ip?uuid=quuqW5QVmC",
                "https://reboot.connect.resocks.net/change-ip?uuid=Cg2dR0YjRf",
                "https://reboot.connect.resocks.net/change-ip?uuid=7kKw0UZprj",
                "https://reboot.connect.resocks.net/change-ip?uuid=R7Z9M3G1g0"
        );
        for (String rotateUrl : rotateUrls) {
            rotateProxyByUrl(rotateUrl);
        }

        List<User> users = userService.findAllByStatus(Status.NOT_IN_USE);
        while (!users.isEmpty()) {
            startUserRegistration();
            users = userService.findAllByStatus(Status.NOT_IN_USE);
        }
    }
}
