package eatda.fixture;

import eatda.domain.member.Member;
import eatda.repository.member.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberGenerator {

    private static final String DEFAULT_NICKNAME = "nickname";
    private static final String DEFAULT_EMAIL = "generatorEmail@example.com";

    private final MemberRepository memberRepository;

    public MemberGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member generate(String socialId) {
        return memberRepository.save(new Member(socialId, DEFAULT_EMAIL, DEFAULT_NICKNAME));
    }

    public Member generateByEmail(String socialId, String email) {
        return memberRepository.save(new Member(socialId, email, DEFAULT_NICKNAME));
    }

    public Member generateByNickname(String socialId, String nickname) {
        return memberRepository.save(new Member(socialId, DEFAULT_EMAIL, nickname));
    }

    public Member generate(String socialId, String email, String nickname) {
        return memberRepository.save(new Member(socialId, email, nickname));
    }

    public Member generateRegisteredMember(String nickname, String email, String socialId, String phoneNumber) {
        return memberRepository.save(new Member(socialId, email, nickname, phoneNumber, true));
    }
}
