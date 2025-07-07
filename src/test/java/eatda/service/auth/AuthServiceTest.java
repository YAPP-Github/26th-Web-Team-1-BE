package eatda.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import eatda.controller.auth.LoginRequest;
import eatda.controller.member.MemberResponse;
import eatda.service.BaseServiceTest;

class AuthServiceTest extends BaseServiceTest {

    @Autowired
    private AuthService authService;

    @Nested
    class Login {

        @Test
        void 로그인_최초_요청_시_회원가입_및_로그인_처리를_한다() {
            LoginRequest request = new LoginRequest("auth_code", "http://localhost:3000");

            MemberResponse response = authService.login(request);

            assertAll(
                    () -> assertThat(response.isSignUp()).isTrue(),
                    () -> assertThat(response.nickname()).isNotNull(),
                    () -> assertThat(response.phoneNumber()).isNull(),
                    () -> assertThat(response.interestArea()).isNull(),
                    () -> assertThat(response.optInMarketing()).isNull()
            );
        }

        @Test
        void 로그인_최초_요청이_아닐_경우_로그인만_처리를_한다() {
            memberGenerator.generate("123");
            LoginRequest request = new LoginRequest("auth_code", "http://localhost:3000");

            MemberResponse response = authService.login(request);

            assertThat(response.isSignUp()).isFalse();
        }
    }
}
