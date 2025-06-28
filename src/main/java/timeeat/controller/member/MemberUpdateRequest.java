package timeeat.controller.member;

import jakarta.validation.constraints.NotBlank;
import timeeat.domain.member.Member;

public record MemberUpdateRequest(@NotBlank String nickname,
                                  @NotBlank String phoneNumber,
                                  @NotBlank String interestArea,
                                  boolean optInMarketing) {

    public Member toMemberUpdater() {
        return new Member(nickname, phoneNumber, interestArea, optInMarketing);
    }
}
