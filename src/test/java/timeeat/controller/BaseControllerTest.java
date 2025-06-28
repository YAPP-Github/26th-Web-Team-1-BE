package timeeat.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import timeeat.DatabaseCleaner;
import timeeat.controller.web.jwt.JwtManager;

@ExtendWith(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {

    private static final List<Filter> SPEC_FILTERS = List.of(new RequestLoggingFilter(), new ResponseLoggingFilter());

    @Autowired
    private JwtManager jwtManager;

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    protected final void setEnvironment() {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
                .addFilters(SPEC_FILTERS)
                .build();
    }

    protected final RequestSpecification given() {
        return RestAssured.given(spec);
    }

    protected final String accessToken() {
        // TODO : 실제 회원 생성
        return jwtManager.issueAccessToken(1L);
    }

    protected final String refreshToken() {
        // TODO : 실제 회원 생성
        return jwtManager.issueRefreshToken(1L);
    }
}
