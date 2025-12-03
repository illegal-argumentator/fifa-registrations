package com.daniel_niepmann.registrations.common.config;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProps mailProps;

    @Bean
    public Store getImapStore() {
        Properties props = new Properties();
        props.put("mail.store.protocol", mailProps.getProtocol());
        props.put("mail.imap.host", mailProps.getHost());
        props.put("mail.imap.port", mailProps.getPort());
        props.put("mail.imap.ssl.enable", mailProps.getSsl());

        Session session = Session.getInstance(props);

        try {
            Store store = session.getStore(mailProps.getProtocol());
            store.connect(mailProps.getUsername(), mailProps.getPassword());
            return store;
        } catch (MessagingException e) {
            log.error("IMAP connection failed: {}", e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
