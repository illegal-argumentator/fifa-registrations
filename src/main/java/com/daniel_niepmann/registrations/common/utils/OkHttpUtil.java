package com.daniel_niepmann.registrations.common.utils;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkHttpUtil {

    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient;

    public <T> T handleApiRequest(Request request, Class<T> responseTarget) {
        String responseContent = handleApiRequest(request);

        try {
            return objectMapper.readValue(responseContent, responseTarget);
        } catch (JacksonException e) {
            logOkHttpUtilError(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public String handleApiRequest(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String message = objectMapper.readValue(response.body().string(), Object.class).toString();
                logOkHttpUtilError(message);
                throw new ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            return response.body().string();
        } catch (IOException e) {
            logOkHttpUtilError(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void logOkHttpUtilError(String message) {
        log.error("OkHttpUtil: {}", message);
    }

}
