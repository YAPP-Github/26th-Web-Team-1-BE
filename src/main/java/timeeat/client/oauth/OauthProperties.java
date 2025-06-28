package timeeat.client.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "oauth")
public class OauthProperties {

    private final String clientId;
    private final String redirectUri;
}
