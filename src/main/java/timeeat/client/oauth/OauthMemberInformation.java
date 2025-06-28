package timeeat.client.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import timeeat.domain.member.Member;

@JsonDeserialize(using = OauthMemberInformationDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OauthMemberInformation(long socialId, String nickname) {

    public Member toMember() {
        return new Member(Long.toString(socialId), nickname);
    }
}

