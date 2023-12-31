package com.oidc.domain.member.domain;

import com.oidc.domain.member.exception.InvalidMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MemberTest {
    @DisplayName("회원을 생성한다.")
    @Test
    void 회원을_생성한다() {
        //given
        String email = "fancy.junyongmoon@gmail.com";
        SocialType socialType = SocialType.GOOGLE;

        // when & then
        assertDoesNotThrow(() -> new Member(email, socialType));
    }


    @DisplayName("회원의 email 형식이 맞지 않으면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"fancy.junyongmoon@", "fancy.junyongmoon@gmail", "fancy.junyongmoon@gmail.com", "fancy.junyongmoon"})
    void 회원의_email_형식이_맞지_않으면_예외가_발생한다(final String email) {

        SocialType socialType = SocialType.GOOGLE;

        // given & when & then
        assertThatThrownBy(() -> new Member(email, socialType))
            .isInstanceOf(InvalidMemberException.class)
            .hasMessage("이메일 형식이 올바르지 않습니다.");
    }
}