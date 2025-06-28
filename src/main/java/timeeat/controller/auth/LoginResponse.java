package timeeat.controller.auth;

import timeeat.controller.member.MemberResponse;

public record LoginResponse(TokenResponse token, MemberResponse information) {

}
