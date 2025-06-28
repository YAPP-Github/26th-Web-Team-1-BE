package timeeat.controller.auth;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import timeeat.controller.web.jwt.JwtManager;
import timeeat.service.auth.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtManager jwtManager;
    private final AuthService authService;

    @GetMapping("/api/auth/login/oauth")
    public ResponseEntity<Void> redirectOauthLoginPage() {
        URI oauthLoginUrl = authService.getOauthLoginUrl();

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(oauthLoginUrl)
                .build();
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<TokenResponse> login(@RequestBody MemberLoginRequest request) {
        authService.login(request);

        TokenResponse response = new TokenResponse(
                jwtManager.issueAccessToken(1L),
                jwtManager.issueRefreshToken(1L));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
