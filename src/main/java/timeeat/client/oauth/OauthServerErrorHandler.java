package timeeat.client.oauth;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

@Component
public class OauthServerErrorHandler implements ErrorHandler {

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        // TODO : 500 에러 처리
        throw new RuntimeException("Oauth server error occurred");
    }
}
