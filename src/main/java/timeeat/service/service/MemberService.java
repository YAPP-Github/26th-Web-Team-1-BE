package timeeat.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timeeat.controller.member.MemberResponse;
import timeeat.controller.member.MemberUpdateRequest;
import timeeat.domain.member.Member;
import timeeat.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse update(long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.getById(memberId);
        Member memberUpdater = request.toMemberUpdater();
        member.update(memberUpdater);
        return new MemberResponse(member);
    }
}
