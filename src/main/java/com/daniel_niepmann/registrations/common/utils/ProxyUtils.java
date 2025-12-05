package com.daniel_niepmann.registrations.common.utils;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class ProxyUtils {

    public static void rotateProxyByUrl(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Failed proxy rotation.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
