package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import com.daniel_niepmann.registrations.web.dto.UserMailVerificationCodeResponse;
import jakarta.mail.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserMailVerificationService {

    private final UserService userService;

    private final Store imapStore;

    private static final int MAX_RETRIES = 5;

    private static final long RETRY_DELAY_MS = 5000;

    public UserMailVerificationCodeResponse verifyUserMail(Long id) {
        User user = userService.findByIdOrThrow(id);

        try {
            Folder inboxFolder = imapStore.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

            String code = null;
            int attempt = 0;

            while (code == null && attempt < MAX_RETRIES) {
                Message[] messages = inboxFolder.getMessages();

                code = Arrays.stream(messages)
                        .filter(message -> isMessageToUser(message, user.getEmail()))
                        .map(this::extractCodeFromMessage)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null);

                if (code == null) {
                    attempt++;
                    System.out.println("Code not found, retrying " + attempt + "/" + MAX_RETRIES);
                    Thread.sleep(RETRY_DELAY_MS);
                    inboxFolder.close(false);
                    inboxFolder.open(Folder.READ_ONLY);
                }
            }

            return UserMailVerificationCodeResponse.builder()
                    .code(code == null ? "" : code)
                    .build();

        } catch (MessagingException | InterruptedException e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private boolean isMessageToUser(Message message, String userEmail) {
        try {
            Address[] to = message.getRecipients(Message.RecipientType.TO);
            if (to == null) return false;

            return Arrays.stream(to)
                    .anyMatch(addr -> addr.toString().contains(userEmail));
        } catch (MessagingException e) {
            return false;
        }
    }

    private String extractCodeFromMessage(Message message) {
        try {
            Object content = message.getContent();

            if (content instanceof String text) {
                return findCode(text);
            } else if (content instanceof Multipart multipart) {
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);
                    if (part.isMimeType("text/plain")) {
                        return findCode((String) part.getContent());
                    } else if (part.isMimeType("text/html")) {
                        String html = (String) part.getContent();
                        String text = html.replaceAll("<[^>]+>", "");
                        return findCode(text);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String findCode(String text) {
        if (text == null) return null;

        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
