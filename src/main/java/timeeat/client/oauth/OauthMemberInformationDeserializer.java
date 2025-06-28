package timeeat.client.oauth;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class OauthMemberInformationDeserializer extends JsonDeserializer<OauthMemberInformation> {

    @Override
    public OauthMemberInformation deserialize(JsonParser jsonParser,
                                              DeserializationContext deserializationContext) throws IOException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);

        long id = root.path("id").asLong();
        String nickname = root
                .path("kakao_account")
                .path("profile")
                .path("nickname")
                .asText(null);
        return new OauthMemberInformation(id, nickname);
    }
}
