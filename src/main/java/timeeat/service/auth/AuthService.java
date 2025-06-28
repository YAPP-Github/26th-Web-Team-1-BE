package timeeat.service.auth;

import java.net.URI;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import timeeat.client.oauth.OauthClient;
import timeeat.client.oauth.OauthMemberInformation;
import timeeat.client.oauth.OauthToken;
import timeeat.controller.auth.MemberLoginRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final OauthClient oauthClient;

    public URI getOauthLoginUrl() {
        return oauthClient.getOauthLoginUrl();
    }

    public void login(MemberLoginRequest request) {
        OauthToken oauthToken = oauthClient.requestOauthToken(request.code());
        OauthMemberInformation oauthMemberInformation = oauthClient.requestMemberInformation(oauthToken);
        log.info("Oauth 로그인 성공: {}", oauthMemberInformation); // TODO 회원 정보 저장 로직 추가
    }
}
