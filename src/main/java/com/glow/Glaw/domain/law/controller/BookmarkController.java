package com.glow.Glaw.domain.law.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.law.service.BookmarkService;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;
import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;
import com.glow.Glaw.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
	private final BookmarkService bookmarkService;

	// 북마크 저장
	@PostMapping("/{lawId}")
	public ResponseEntity<ApiResponse<Void>> saveBookmark(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PathVariable Long lawId
	) {
		if (user == null) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		bookmarkService.saveBookmark(user.getUserId(), lawId);

		return ResponseEntity.ok(
			ApiResponse.success("북마크 저장 성공", null)
		);
	}

	// 북마크 삭제
	@DeleteMapping("/{lawId}")
	public ResponseEntity<ApiResponse<Void>> deleteBookmark(
		@AuthenticationPrincipal CustomOAuth2User user,
		@PathVariable Long lawId
	) {
		if (user == null) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		bookmarkService.deleteBookmark(user.getUserId(), lawId);

		return ResponseEntity.ok(
			ApiResponse.success("북마크 삭제 성공", null)
		);
	}
}
