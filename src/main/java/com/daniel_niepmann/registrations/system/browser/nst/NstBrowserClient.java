package com.daniel_niepmann.registrations.system.browser.nst;

import com.daniel_niepmann.registrations.common.utils.OkHttpUtil;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.GetProfilesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NstBrowserClient {

    @Value("${nst-browser.api-key}")
    private String NST_API_KEY;

    @Value("${nst-browser.group_id}")
    private String NST_GROUP_ID;

    @Value("${nst-browser.host}")
    private String host;

    @Value("${nst-browser.port}")
    private Long port;

    private final ObjectMapper objectMapper;

    private final OkHttpUtil okHttpUtil;

    private static final String X_API_KEY_HEADER = "x-api-key";

    private final String NST_API_BASE_URL = "http://%s:%d/api/v2".formatted(host, port);

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
