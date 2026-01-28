package com.glow.Glaw.global.auth.login.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.domain.user.domain.repository.UserRepository;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;
import com.glow.Glaw.global.auth.login.dto.OAuthAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 사용자 정보 파싱 후 DB조회, 존재하면 로그인 / 존재하지 않으면 회원가입 후 로그인 -> SecurityContext에 넣을 CustomOAuth2User 반환
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		// 1. Google에서 유저 정보(attributes) 받아오기
		OAuth2User oAuth2User = super.loadUser(userRequest);

		// attributes 꺼내기
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// 어떤 OAuth provider인지 확인
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		log.info("OAuth2 provider: {}", registrationId);
		log.info("OAuth2 attributes: {}", attributes);

		// 2. attriutes -> OAuthAttributes 형태로 변환 (GoogleOAuth2UserInfo 사용)
		OAuthAttributes extractAttributes = OAuthAttributes.of(registrationId, attributes);

		// 3. 회원 조회 또는 생성
		User user = saveOrUpdate(extractAttributes);

		// 4. SecurityContext에 저장할 CustomOAuth2User 반환 -> SecurityContext에 넣을 CustomOAuth2User 반환
		return new CustomOAuth2User(
			user.getId(),
			user.getEmail(),
			user.getName(),
			attributes
		);
	}

	private User saveOrUpdate(OAuthAttributes attributes) {
		return userRepository.findByEmail(attributes.getEmail())
			.orElseGet(() -> {
				// 회원가입
				User newUser = attributes.toEntity();
				return userRepository.save(newUser);
			});
	}
}
