package timeeat.client.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OauthMemberInformationDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OauthMemberInformation(long socialId, String nickname) {
}

