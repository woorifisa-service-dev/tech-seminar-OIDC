package com.oidc.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member save(final Member member) {
        return memberRepository.save(member);
    }

    public Member findByEmail(final String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(NoSuchMemberException::new);
    }

    public boolean existByEmail(final String email) {
        return memberRepository.existsByEmail(email);
    }
}
