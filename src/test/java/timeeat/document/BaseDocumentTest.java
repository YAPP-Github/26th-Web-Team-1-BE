package timeeat.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationConfigurer;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import timeeat.controller.web.jwt.JwtManager;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseDocumentTest {

    // @MockitoBean 을 이용하여 Service Layer 및 특정 객체 Mocking

    @Autowired
    private JwtManager jwtManager;

    @LocalServerPort
    private int port;

    private RequestSpecification spec;


    @BeforeEach
    void setEnvironment(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        RestAssuredRestDocumentationConfigurer webConfigurer =
                RestAssuredRestDocumentation.documentationConfiguration(restDocumentation);
        spec = new RequestSpecBuilder()
                .addFilter(webConfigurer)
                .build();
    }

    protected final RestDocsRequest request() {
        return new RestDocsRequest();
    }

    protected final RestDocsResponse response() {
        return new RestDocsResponse();
    }

    protected final RestDocsFilterBuilder document(String identifierPrefix, int statusCode) {
        return new RestDocsFilterBuilder(identifierPrefix, Integer.toString(statusCode));
    }

    protected final RequestSpecification given(RestDocumentationFilter documentationFilter) {
        return RestAssured.given(spec)
                .filter(documentationFilter);
    }

    protected final String accessToken() {
        return jwtManager.issueAccessToken(1L);
    }

    protected final String refreshToken() {
        return jwtManager.issueRefreshToken(1L);
    }
}
