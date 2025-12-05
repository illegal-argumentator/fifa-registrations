package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.daniel_niepmann.registrations.common.utils.WaitUtils.waitSafely;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;

    private final EntityManager entityManager;

    @Transactional
    public List<User> waitForUsersInProgress() {
        int attempts = 1;
        waitSafely(5000);

        List<User> usersInProgress = userService.findAllByStatus(Status.IN_PROGRESS);

        while (attempts < 30 && usersInProgress.isEmpty()) {
            log.info("Waiting for users to become in progress: {}/30", attempts);
            waitSafely(5000);

            entityManager.clear();
            usersInProgress = userService.findAllByStatus(Status.IN_PROGRESS);

            attempts++;
        }

        if (usersInProgress.isEmpty()) {
            throw new ApiException("Not found users in progress.", HttpStatus.NOT_FOUND.value());
        }

        waitSafely(500);

        return usersInProgress;
    }

    @Transactional
    public void waitForUsersToCompleteRegistration(List<Long> userIds) {
        int attempts = 1;
        List<User> users = userService.findAllByIdIn(userIds);

        while (attempts <= 40 && existsInProgress(users)) {
            log.info("Waiting for all users to complete: {}/40", attempts);
            waitSafely(10_000);

            entityManager.clear();
            users = userService.findAllByIdIn(userIds);

            attempts++;
        }

         users.forEach(user -> {
            if (user.getStatus() == Status.IN_PROGRESS) {
                log.warn("User {} registration time out.", user.getId());
                userService.update(user.getId(), User.builder()
                        .status(Status.NOT_IN_USE)
                        .errorMessage("Registration time out.")
                        .build());
            }
        });
    }

    private boolean existsInProgress(List<User> users) {
        return users.stream()
                .anyMatch(user -> user.getStatus() == Status.IN_PROGRESS);
    }
}
