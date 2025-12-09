package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.daniel_niepmann.registrations.common.utils.WaitUtils.waitSafely;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    public List<User> waitForUsersInProgress() {
        int attempts = 1;
        waitSafely(5000);

        List<User> usersInProgress = userService.findAllByStatus(Status.IN_PROGRESS);

        while (attempts < 30 && usersInProgress.isEmpty()) {
            waitSafely(5000);
            log.info("Waiting for users to become in progress: {}/30", attempts);
            if (attempts %7 == 6){
                transactionTemplate.execute(it -> {
                    entityManager.clear();
                    return null;
                });
            }
            usersInProgress = userService.findAllByStatus(Status.IN_PROGRESS);

            attempts++;
        }

        waitSafely(500);

        return usersInProgress;
    }

    public void waitForUsersToCompleteRegistration(List<Long> userIds) {
        int attempts = 1;
        int maxAttempts = 16;

        while (attempts <= maxAttempts) {
            log.info("Waiting for all users to complete: {}/{}", attempts, maxAttempts);

            // Always fetch fresh data in a new transaction to avoid cache issues
            List<User> users = transactionTemplate.execute(status -> {
                entityManager.clear(); // Clear cache before each fetch
                return userService.findAllByIdIn(userIds);
            });

            // Check if any users are still in progress
            if (users == null || !existsInProgress(users)) {
                log.info("All users completed registration after {} attempts", attempts);
                break; // Exit early if all users are done
            }

            // Log current user statuses for debugging every 5 attempts
            if (attempts % 5 == 0) {
                users.forEach(user ->
                    log.debug("User {} status: {}", user.getId(), user.getStatus())
                );
            }

            attempts++;

            // Don't sleep on the last attempt
            if (attempts <= maxAttempts) {
                waitSafely(10_000);
            }
        }

        if (attempts < maxAttempts) {
            var size = 10;
            for (int i = 0; i < size; i++) {
                waitSafely(5_700);
                log.info("Finalizing wait for users to complete: attempt {}/{}", i + 1,size);
            }
        }


        // Handle timed out users - fetch one more time to be sure
        List<User> finalUsers = transactionTemplate.execute(status -> {
            entityManager.clear();
            return userService.findAllByIdIn(userIds);
        });

        // Update timed out users in a separate transaction
        if (finalUsers != null) {
            transactionTemplate.execute(status -> {
                finalUsers.stream()
                    .filter(user -> user.getStatus() == Status.IN_PROGRESS)
                    .forEach(user -> {
                        log.warn("User {} registration time out.", user.getId());
                        userService.update(user.getId(), User.builder()
                                .status(Status.NOT_IN_USE)
                                .errorMessage("Registration time out.")
                                .build());
                    });
                return null;
            });
        }
    }

    private boolean existsInProgress(List<User> users) {
        List<User> inProgressUsers = users.stream()
                .filter(user -> user.getStatus() == Status.IN_PROGRESS)
                .toList();

        if (!inProgressUsers.isEmpty()) {
            log.debug("Found {} users still in progress: {}",
                inProgressUsers.size(),
                inProgressUsers.stream().map(User::getId).toList());
        }

        return !inProgressUsers.isEmpty();
    }
}
