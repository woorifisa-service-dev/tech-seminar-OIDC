package com.oidc.infrastructure.oauth.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.oidc.auth.dto.OAuthMember;
import com.oidc.auth.support.OAuthClient;
import com.oidc.infrastructure.dto.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GoogleOAuthClient implements OAuthClient {

    private static final String JWT_DELIMITER = "\\.";

    private final String googleRedirectUri;
    private final String googleClientId;
    private final String googleClientSecret;
    private final String googleTokenUri;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleOAuthClient(
        @Value("${oauth.google.redirect_uri}") final String googleRedirectUri,
        @Value("${oauth.google.client_id}") final String googleClientId,
        @Value("${oauth.google.client_secret}") final String googleClientSecret,
        @Value("${oauth.google.token_uri}") final String googleTokenUri,
        final RestTemplate restTemplate, final ObjectMapper objectMapper) {
            this.googleRedirectUri = googleRedirectUri;
            this.googleClientId = googleClientId;
            this.googleClientSecret = googleClientSecret;
            this.googleTokenUri = googleTokenUri;
            this.restTemplate = restTemplate;
            this.objectMapper = objectMapper;
        }
    @Override
    public OAuthMember getOAuthMember(final String code) {
        GoogleTokenResponse googleTokenResponse = requestGoogleToken(code);
        String payload = getPayloadFrom(googleTokenResponse.getIdToken());

        String decodedPayload = decodeJwtPayload(payload);

        try {
            return generateOAuthMemberBy(decodedPayload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    // 인가코드 & 필수 파라미터를 포함해서 POST - 액세스 토큰 요청
    private GoogleTokenResponse requestGoogleToken(final String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = generateRequestParams(code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.postForEntity(googleTokenUri, request, GoogleTokenResponse.class).getBody();
    }

    private MultiValueMap<String, String> generateRequestParams(final String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", googleRedirectUri);
        return params;
    }

    private String getPayloadFrom(final String jwt) {
        return jwt.split(JWT_DELIMITER)[1];
    }

    private String decodeJwtPayload(final String payload) {
        return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    }

    // 수정 완료
    private OAuthMember generateOAuthMemberBy(final String decodedIdToken) throws JsonProcessingException {
        Map<String, String> userInfo = objectMapper.readValue(decodedIdToken, HashMap.class);
        String email = userInfo.get("email");

        return new OAuthMember(email);
    }
}
