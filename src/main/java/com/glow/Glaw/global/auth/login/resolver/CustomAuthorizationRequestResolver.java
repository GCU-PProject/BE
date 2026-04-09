package com.glow.Glaw.global.auth.login.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
	private final OAuth2AuthorizationRequestResolver defaultResolver;

	public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
		this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
			clientRegistrationRepository, "/oauth2/authorization"
		);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
		return customizeAuthorizationRequest(request, authorizationRequest);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
		return customizeAuthorizationRequest(request, authorizationRequest);
	}

	private OAuth2AuthorizationRequest customizeAuthorizationRequest(
		HttpServletRequest request,
		OAuth2AuthorizationRequest authorizationRequest
	) {
		if (authorizationRequest == null) {
			return null;
		}

		// 프론트에서 보낸 redirect_uri를 세션에 저장
		String redirectUri = request.getParameter("redirect_uri");
		if (redirectUri != null && !redirectUri.isEmpty()) {
			request.getSession().setAttribute("CUSTOM_REDIRECT_URI", redirectUri);
		}

		return authorizationRequest;
	}
}
