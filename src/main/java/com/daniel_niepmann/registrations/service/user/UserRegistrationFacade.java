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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
        List<String> proxies = Stream.of(
                "http://NZV3jU5C:1LvF7Hc4@connect.resocks.net:8080",
                "http://3YxEFmJ6:ZDW188xw@connect.resocks.net:8080",
                "http://w17q2ptd:p6wofJCa@connect.resocks.net:8080",


                "http://Z3yIy68g:uAT0m2dW@connect.resocks.net:8080",
                "http://HtNqmc6p:ZGTIhJdg@connect.resocks.net:8080",
                "http://dpzvVafz:OKlC5ZFj@connect.resocks.net:8080",

                "http://PpVdU680:o2KG2llC@connect.resocks.net:8080",
                "http://nivkPLZ0:6BzWOmrl@connect.resocks.net:8080",
                "http://rtgtl2ov:J4rsYQ0C@connect.resocks.net:8080",

                "http://0GJ9J7gg:JdaE2Zi5@connect.resocks.net:8080"
        ).limit(13).toList();
        rotate();
        //
        List<String> profileIds = new ArrayList<>();
        for (int i = 0; i < proxies.size(); i++) {
            var proxy = proxies.get(i);
            var req = CreateProfileRequest.builder()
                    .groupId(NST_GROUP_ID)
                    .proxy(proxy)
                    .fingerprint(new CreateProfileRequest.Fingerprint(
                            new CreateProfileRequest.Fingerprint.Screen(
                                    1280,
                                    1080
                            )
                    ))
                    .build();
            try {
                CreateProfileResponse nstBrowserClientProfile = nstBrowserClient.createProfile(i, req);
                profileIds.add(nstBrowserClientProfile.getData().getProfileId());
            }catch (Exception e){
                log.error("Error creating profile with proxy {}: {}", proxy, e.getMessage());
            }
        }

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

    private void rotate() {
        List<String> rotateLinks = List.of(
                "https://reboot.connect.resocks.net/change-ip?uuid=JKyMqgXEWf",
                "https://reboot.connect.resocks.net/change-ip?uuid=8auIqqHRCr",
                "https://reboot.connect.resocks.net/change-ip?uuid=e8QyvytL6G",
                "https://reboot.connect.resocks.net/change-ip?uuid=QSX1uCeAQ4",
                "https://reboot.connect.resocks.net/change-ip?uuid=YjfGKQAdGY",
                "https://reboot.connect.resocks.net/change-ip?uuid=GO1CwPDkOb",
                "https://reboot.connect.resocks.net/change-ip?uuid=YEKK9KBDho",
                "https://reboot.connect.resocks.net/change-ip?uuid=jQpctHF58I",
                "https://reboot.connect.resocks.net/change-ip?uuid=0K06fT51RY",
                "https://reboot.connect.resocks.net/change-ip?uuid=57RJclQPdZ"
        );
        for (String rotateLink : rotateLinks) {
            rotateProxyByUrl(rotateLink);
        }
    }

    public void processUsersRegistration() {
        rotate();

        List<User> users = userService.findAllByStatus(Status.NOT_IN_USE);
        Set<String> deletedProfileIds = new HashSet<>();
        while (!users.isEmpty()) {
            GetProfilesResponse profilesByCursor = nstBrowserClient.getProfilesByCursor();
            List<String> profileIds = profilesByCursor.data().profiles().stream()
                    .map(Profile::profileId)
                    .filter(deletedProfileIds::add)
                    .limit(0)
                    .toList();
            nstBrowserService.killAllBrowsers(profileIds);

            startUserRegistration();
            users = userService.findAllByStatus(Status.NOT_IN_USE);
        }
    }
}
