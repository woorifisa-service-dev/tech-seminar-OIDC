package com.oidc.config;

import com.oidc.auth.support.OAuthClient;
import com.oidc.infrastructure.oauth.client.StubOAuthClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    // SpringBootTest 환경에서 OAuthClient 실제 객체 대신 테스트 더블을 빈으로 등록하기 위한 코드
    @Bean
    public OAuthClient oAuthClient() {
        return new StubOAuthClient();
    }
}