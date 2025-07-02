package timeeat.controller.auth;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import timeeat.controller.member.MemberResponse;
import timeeat.controller.web.jwt.JwtManager;
import timeeat.service.auth.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtManager jwtManager;
    private final AuthService authService;

    @GetMapping("/api/auth/login/oauth")
    public ResponseEntity<Void> redirectOauthLoginPage(@RequestHeader(HttpHeaders.ORIGIN) String origin) {
        URI oauthLoginUrl = authService.getOauthLoginUrl(origin);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(oauthLoginUrl)
                .build();
    }

    // TODO : login() ControllerTest, DocumentTest 수정
    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                               @RequestHeader(HttpHeaders.ORIGIN) String origin) {
        MemberResponse member = authService.login(request, origin);
        TokenResponse token = new TokenResponse(
                jwtManager.issueAccessToken(member.id()),
                jwtManager.issueRefreshToken(member.id()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new LoginResponse(token, member));
    }

    @PostMapping("/api/auth/reissue")
    public ResponseEntity<TokenResponse> reissueToken(@RequestBody ReissueRequest request) {
        long id = jwtManager.resolveRefreshToken(request.refreshToken());

        TokenResponse response = new TokenResponse(
                jwtManager.issueAccessToken(id),
                jwtManager.issueRefreshToken(id));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
