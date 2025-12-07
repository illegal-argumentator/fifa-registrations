package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import com.daniel_niepmann.registrations.system.browser.nst.NstBrowserClient;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.CreateProfileRequest;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.CreateProfileResponse;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.GetProfilesResponse;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.embedded.Profile;
import com.daniel_niepmann.registrations.system.browser.nst.service.NstBrowserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Value("${nst-browser.group_id}")
    private String NST_GROUP_ID;

    public void startUserRegistration() {

        // TODO create profiles with proxies
            List<String> proxies = List.of(
                    "http://NZV3jU5C:1LvF7Hc4@connect.resocks.net:8080",
                    "http://w17q2ptd:p6wofJCa@connect.resocks.net:8080",
                    "http://3YxEFmJ6:ZDW188xw@connect.resocks.net:8080",
                    "http://UBJCFiDg:6FLUpyvo@connect.resocks.net:8080",
                    "http://t7BWqEO9:17lEPVFp@connect.resocks.net:8080",
                    "http://jNFFY7K3:x1MzLJhV@connect.resocks.net:8080",
                    "http://0F2PB5Xj:HITheYRb@connect.resocks.net:8080",
                    "http://8TL6oFXR:kaSXYFiY@connect.resocks.net:8080",
                    "http://rBAH9tsn:oB9X2Dj9@connect.resocks.net:8080",
                    "http://6CGZQsa7:cyyHj5HL@connect.resocks.net:8080"
                    );

            List<String> profileIds = new ArrayList<>();
            proxies.forEach(proxy -> {
                CreateProfileResponse nstBrowserClientProfile = nstBrowserClient.createProfile(CreateProfileRequest.builder()
                        .groupId(NST_GROUP_ID)
                        .proxy(proxy)
                        .build());
                profileIds.add(nstBrowserClientProfile.getData().getProfileId());
            });
            if (profileIds.isEmpty()) throw new ApiException("No profiles in NST browser.", HttpStatus.NO_CONTENT.value());

        try {
            nstBrowserClient.startBrowsers(profileIds);
            List<User> users = userRegistrationService.waitForUsersInProgress();

            if (users.isEmpty()) {
                return;
            }

            userRegistrationService.waitForUsersToCompleteRegistration(users.stream().map(User::getId).toList());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            userService.failAllUsersInProgress();
            throw exception;
        }
    }

    public void processUsersRegistration() {
        List<String> rotateLinks = List.of(
                "https://reboot.connect.resocks.net/change-ip?uuid=JKyMqgXEWf",
                "https://reboot.connect.resocks.net/change-ip?uuid=8auIqqHRCr",
                "https://reboot.connect.resocks.net/change-ip?uuid=e8QyvytL6G",
                "https://reboot.connect.resocks.net/change-ip?uuid=v6ZcH1QTED0",
                "https://reboot.connect.resocks.net/change-ip?uuid=WCB7nI6F3V",
                "https://reboot.connect.resocks.net/change-ip?uuid=NxJDluZCcb",
                "https://reboot.connect.resocks.net/change-ip?uuid=quuqW5QVmC",
                "https://reboot.connect.resocks.net/change-ip?uuid=Cg2dR0YjRf",
                "https://reboot.connect.resocks.net/change-ip?uuid=7kKw0UZprj",
                "https://reboot.connect.resocks.net/change-ip?uuid=R7Z9M3G1g0"
        );
        for (String rotateLink : rotateLinks) {
            rotateProxyByUrl(rotateLink);
        }

        List<User> users = userService.findAllByStatus(Status.NOT_IN_USE);
        while (!users.isEmpty()) {
            GetProfilesResponse profilesByCursor = nstBrowserClient.getProfilesByCursor();
            List<String> profileIds = profilesByCursor.data().profiles().stream().map(Profile::profileId).toList();
            nstBrowserService.killAllBrowsers(profileIds);

            startUserRegistration();
            users = userService.findAllByStatus(Status.NOT_IN_USE);
        }
    }
}
