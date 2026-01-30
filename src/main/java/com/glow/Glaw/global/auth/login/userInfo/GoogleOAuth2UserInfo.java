package com.glow.Glaw.global.auth.login.userInfo;

import java.util.Map;

// Google에서 정보 받아올 구조 만들기
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {
	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}
}
