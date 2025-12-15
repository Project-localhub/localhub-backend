package com.localhub.localhub.OAuth2;

import java.util.Map;

public class KaKaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    public KaKaoResponse(Map<String, Object> attributes) {

        this.attributes = attributes;

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
        return
                ((Map<String, String>) attributes.get("kakao_account")).get("email").toString();
    }

    @Override
    public String getName() {
        return ((Map<String, String>) attributes.get("kakao_account")).get("name").toString();
    }
}