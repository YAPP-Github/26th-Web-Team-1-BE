package timeeat.fixture;

import org.springframework.stereotype.Component;
import timeeat.domain.member.Member;
import timeeat.repository.member.MemberRepository;

@Component
public class MemberGenerator {

    private final MemberRepository memberRepository;

    public MemberGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member generate(String socialId) {
        return memberRepository.save(new Member(socialId, "nickname"));
    }
}
