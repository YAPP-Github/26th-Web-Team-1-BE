package timeeat.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import timeeat.controller.web.auth.LoginMember;
import timeeat.service.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PutMapping("/api/member")
    public ResponseEntity<MemberResponse> updateMember(LoginMember member, @RequestBody MemberUpdateRequest request) {
        return null;
    }
}
