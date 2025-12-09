package com.daniel_niepmann.registrations.system.browser.nst;

import com.daniel_niepmann.registrations.common.utils.OkHttpUtils;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.CreateProfileRequest;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.CreateProfileResponse;
import com.daniel_niepmann.registrations.system.browser.nst.common.dto.GetProfilesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Random;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NstBrowserClient {

    @Value("${nst-browser.api-key}")
    private String NST_API_KEY;

    @Value("${nst-browser.group_id}")
    private String NST_GROUP_ID;

    @Value("${nst-browser.url}")
    private String NST_API_BASE_URL;

    private final ObjectMapper objectMapper;

    private final OkHttpUtils okHttpUtils;

    private static final String X_API_KEY_HEADER = "x-api-key";

    private static final List<NtsContainerInfo> NTS_CONTAINERS = List.of(
            new NtsContainerInfo("157.90.209.160", 8003),
            new NtsContainerInfo("157.90.209.160", 8002),
            new NtsContainerInfo("157.90.209.160", 8000),
            new NtsContainerInfo("157.90.209.160", 8004),
            new NtsContainerInfo("157.90.209.160", 8005),
            new NtsContainerInfo("157.90.209.160", 8006),
            new NtsContainerInfo("157.90.209.160", 8007),
            new NtsContainerInfo("157.90.209.160", 8008),
            new NtsContainerInfo("157.90.209.160", 8009),
            new NtsContainerInfo("157.90.209.160", 8010)
    );

    private record NtsContainerInfo(String host, int port) {}

    public GetProfilesResponse getProfilesByCursor() {
        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/profiles/cursor?pageSize=100&groupId=" + NST_GROUP_ID)
                .get()
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        return okHttpUtils.handleApiRequest(request, GetProfilesResponse.class);
    }

    public void clearProfileCache(String profileId) {
        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/local/profiles/" + profileId)
                .delete()
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtils.handleApiRequest(request);
    }

    public void clearProfileCookies(String profileId) {
        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/local/profiles/" + profileId + "/cookies")
                .delete()
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtils.handleApiRequest(request);
    }

    public void startBrowsers(List<String> profileIds) {

        for (int i = 0; i < profileIds.size(); i++) {
            String json = objectMapper.writeValueAsString(List.of(profileIds.get(i)));
            var nstContainer = NTS_CONTAINERS.get(i % NTS_CONTAINERS.size());
            var url = "http://" + nstContainer.host + ":" + nstContainer.port + "/api/v2/browsers";
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                    .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                    .build();

            try {
                Thread.sleep(5000, new Random().nextInt(5000));
                okHttpUtils.handleApiRequest(request);
            }catch (Exception e){

            }


        }



    }

    public void stopBrowsers(List<String> profileIds) {
        for (int i = 0; i < profileIds.size(); i++) {
            for (int j = 0; j < NTS_CONTAINERS.size(); j++) {
                String json = objectMapper.writeValueAsString(List.of(profileIds.get(i)));
                var nstContainer = NTS_CONTAINERS.get(j % NTS_CONTAINERS.size());
                var url = "http://" + nstContainer.host + ":" + nstContainer.port + "/api/v2/browsers";
                Request request = new Request.Builder()
                        .url(url)
                        .delete(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                        .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                        .build();

                try {
                    okHttpUtils.handleApiRequest(request);
                    break;
                }catch (Exception e){
//                    log.warn("Error stopping browser for profile {}: {}", profileIds.get(i), e.getMessage());
                }
            }
        }
    }

    public CreateProfileResponse createProfile(int index, CreateProfileRequest createProfileRequest) {
        String json = objectMapper.writeValueAsString(createProfileRequest);

        var nstContainer = NTS_CONTAINERS.get(index % NTS_CONTAINERS.size());
        var url = "http://" + nstContainer.host + ":" + nstContainer.port + "/api/v2/profiles";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        return okHttpUtils.handleApiRequest(request, CreateProfileResponse.class);
    }

    public CreateProfileResponse createProfile(CreateProfileRequest createProfileRequest) {
        String json = objectMapper.writeValueAsString(createProfileRequest);

        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/profiles")
                .post(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        return okHttpUtils.handleApiRequest(request, CreateProfileResponse.class);
    }

    public void deleteProfiles(List<String> profileIds) {
        String json = objectMapper.writeValueAsString(profileIds);

        Request request = new Request.Builder()
                .url(NST_API_BASE_URL + "/profiles")
                .delete(RequestBody.create(json, MediaType.get(APPLICATION_JSON_VALUE)))
                .addHeader(X_API_KEY_HEADER, NST_API_KEY)
                .build();

        okHttpUtils.handleApiRequest(request);
    }
}
