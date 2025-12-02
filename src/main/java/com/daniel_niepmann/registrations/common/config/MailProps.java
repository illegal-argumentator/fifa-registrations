package com.daniel_niepmann.registrations.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("mail")
public class MailProps {

    private String protocol;

    private String host;

    private Integer port;

    private Boolean ssl;

    private String username;

    private String password;

}
