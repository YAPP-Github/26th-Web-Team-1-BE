package eatda.controller.member;

import eatda.domain.member.Member;

public record MemberResponse(long id,
                             String email,
                             boolean isSignUp,
                             String nickname,
                             String phoneNumber,
                             Boolean optInMarketing
) {

    public MemberResponse(Member member, boolean isSignUp) {
        this(member.getId(),
                member.getEmail(),
                isSignUp,
                member.getNickname(),
                member.getPhoneNumber(),
                member.getOptInMarketing());
    }

    public MemberResponse(Member member) {
        this(member, false);
    }
}
