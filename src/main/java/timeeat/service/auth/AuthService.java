package timeeat.service.auth;

import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timeeat.client.oauth.OauthClient;
import timeeat.client.oauth.OauthMemberInformation;
import timeeat.client.oauth.OauthToken;
import timeeat.controller.auth.LoginRequest;
import timeeat.controller.member.MemberResponse;
import timeeat.domain.member.Member;
import timeeat.repository.member.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OauthClient oauthClient;
    private final MemberRepository memberRepository;

    public URI getOauthLoginUrl() {
        return oauthClient.getOauthLoginUrl();
    }

    @Transactional
    public MemberResponse login(LoginRequest request) {
        OauthToken oauthToken = oauthClient.requestOauthToken(request.code());
        OauthMemberInformation oauthInformation = oauthClient.requestMemberInformation(oauthToken);

        Optional<Member> optionalMember = memberRepository.findBySocialId(Long.toString(oauthInformation.socialId()));
        boolean isFirstLogin = optionalMember.isEmpty();
        return new MemberResponse(
                optionalMember.orElseGet(() -> memberRepository.save(oauthInformation.toMember())),
                isFirstLogin);
    }
}
