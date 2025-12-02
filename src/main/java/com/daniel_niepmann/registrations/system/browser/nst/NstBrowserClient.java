package com.daniel_niepmann.registrations.system.browser.nst;

import com.daniel_niepmann.registrations.common.exception.ApiException;
import com.daniel_niepmann.registrations.common.utils.OkHttpUtil;
import com.daniel_niepmann.registrations.domain.proxy.model.Proxy;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.CreateProfileResponse;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.GetProfilesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.daniel_niepmann.registrations.system.browser.nst.common.dto.builder.CreateProfileRequestBuilder.buildCreateProfileRequest;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NstBrowserClient {

    @Value("${nst-browser.api-key}")
    private String NST_API_KEY;

    @Value("${nst-browser.group_id}")
    private String NST_GROUP_ID;

    private final ObjectMapper objectMapper;

    private final OkHttpUtil okHttpUtil;

    private static final String X_API_KEY_HEADER = "x-api-key";

    private static final String NST_API_BASE_URL = "http://localhost:8848/api/v2";

    @Deprecated
    public CreateProfileResponse createProfile(String profileName, Proxy proxy) {
        try {
            String json = objectMapper.writeValueAsString(buildCreateProfileRequest(profileName, proxy));

            Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/profiles")
                .post(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

            return okHttpUtil.handleApiRequest(request, CreateProfileResponse.class);
        } catch (JacksonException exception) {
            log.error("NstBrowserClient: {}", exception.getMessage());
            throw new ApiException("Couldn't create profile in Nst browser", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public GetProfilesResponse getProfilesByCursor() {
        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/profiles/cursor?pageSize=100&groupId=" + NST_GROUP_ID)
                .get()
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        return okHttpUtil.handleApiRequest(request, GetProfilesResponse.class);
    }

    public void clearProfileCache(String profileId) {
        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/local/profiles/" + profileId)
                .delete()
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtil.handleApiRequest(request);
    }

    public void clearProfileCookies(String profileId) {
        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/local/profiles/" + profileId + "/cookies")
                .delete()
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtil.handleApiRequest(request);
    }

    public void startBrowsers(List<String> profileIds) {
        String json = objectMapper.writeValueAsString(profileIds);

        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/browsers")
                .post(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtil.handleApiRequest(request);
    }

    public void stopBrowsers(List<String> profileIds) {
        String json = objectMapper.writeValueAsString(profileIds);

        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/browsers")
                .delete(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtil.handleApiRequest(request);
    }
}
