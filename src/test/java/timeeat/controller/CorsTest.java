package timeeat.controller;

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;

public class CorsTest extends BaseControllerTest {

    @Value("${cors.origin}")
    private String corsOrigin;

    @Nested
    class PreflightTest {

        @ParameterizedTest
        @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE"})
        void CORS_preflight에서_허용된_origin의_요청을_정상적으로_처리할_수_있다(String method) {
            given()
                    .header("Origin", corsOrigin)
                    .header("Access-Control-Request-Method", method)
                    .when().options("/")
                    .then().statusCode(200)
                    .headers("Access-Control-Allow-Origin", corsOrigin)
                    .header("Access-Control-Allow-Methods", containsString(method));
        }

        @Test
        void CORS_preflight에서_허용되지_않은_origin의_요청을_막을_수_있다() {
            String notAllowedOrigin = "https://not-allowed-origin.com";
            String allowedMethod = "GET";

            given()
                    .header("Origin", notAllowedOrigin)
                    .header("Access-Control-Request-Method", allowedMethod)
                    .when().options("/")
                    .then().statusCode(403);
        }
    }
}
