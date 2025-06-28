package timeeat.controller.member;

import timeeat.domain.member.Member;

public record MemberResponse(long id,
                             boolean isSignUp,
                             String nickname,
                             String phoneNumber,
                             String interestArea,
                             Boolean optInMarketing
) {

    public MemberResponse(Member member, boolean isSignUp) {
        this(member.getId(),
                isSignUp,
                member.getNickname(),
                member.getPhoneNumber(),
                member.getInterestAreaName(),
                member.getOptInMarketing());
    }

    public MemberResponse(Member member) {
        this(member, false);
    }
}
