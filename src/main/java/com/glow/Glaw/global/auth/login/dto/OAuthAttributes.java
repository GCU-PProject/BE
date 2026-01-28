package com.glow.Glaw.global.auth.login.dto;

import java.util.Map;

import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.global.auth.login.userInfo.GoogleOAuth2UserInfo;
import com.glow.Glaw.global.auth.login.userInfo.OAuth2UserInfo;

// 받아온 정보를 User entity로 변환
public class OAuthAttributes {
	private final OAuth2UserInfo oAuth2UserInfo;

	private OAuthAttributes(OAuth2UserInfo oAuth2UserInfo) {
		this.oAuth2UserInfo = oAuth2UserInfo;
	}

	// provider 구분 후 해당 UserInfo 객체 생성
	public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
		if ("google".equals(registrationId)) {
			return new OAuthAttributes(new GoogleOAuth2UserInfo(attributes));
		}

		// google 이외의 요청이 들어왔을 때
		throw new IllegalArgumentException("Unsupported provider: " + registrationId);
	}

	// OAuth2UserInfo를 User 엔티티로 변환
	public User toEntity() {
		return User.createUser(
			oAuth2UserInfo.getEmail(),
			oAuth2UserInfo.getName()
		);
	}

	public String getEmail() {
		return oAuth2UserInfo.getEmail();
	}

	public String getName() {
		return oAuth2UserInfo.getName();
	}
}
