package com.daniel_niepmann.registrations.common.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {

        return new OkHttpClient().newBuilder()
                .callTimeout(Duration.ofMinutes(1))
                .connectTimeout(Duration.ofMinutes(1))
                .readTimeout(Duration.ofMinutes(1))
                .writeTimeout(Duration.ofMinutes(1))
                .build();
    }
}
