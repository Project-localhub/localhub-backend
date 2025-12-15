package com.localhub.localhub.OAuth2;

import java.util.Map;

public class KaKaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KaKaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : null;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount != null
                ? (String) kakaoAccount.get("email")
                : null;
    }

    @Override
    public String getName() {
        return profile != null
                ? (String) profile.get("nickname")
                : null;
    }
}