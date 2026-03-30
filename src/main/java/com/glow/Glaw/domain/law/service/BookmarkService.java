package com.glow.Glaw.domain.law.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glow.Glaw.domain.law.domain.Law;
import com.glow.Glaw.domain.law.domain.UserLaw;
import com.glow.Glaw.domain.law.repository.LawRepository;
import com.glow.Glaw.domain.law.repository.UserLawRepository;
import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.domain.user.repository.UserRepository;
import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {
	private final UserLawRepository userLawRepository;
	private final UserRepository userRepository;
	private final LawRepository lawRepository;

	// 북마크 저장
	public void saveBookmark(Long userId, Long lawId) {
		if (userLawRepository.existsByUser_IdAndLaw_Id(userId, lawId)) {
			return;
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		Law law = lawRepository.findById(lawId)
			.orElseThrow(() -> new CommonException(ErrorCode.NO_RESOURCE_FOUND));

		userLawRepository.save(new UserLaw(user, law));
	}

	// 북마크 삭제
	public void deleteBookmark(Long userId, Long lawId) {
		if (!userLawRepository.existsByUser_IdAndLaw_Id(userId, lawId)) {
			throw new CommonException(ErrorCode.NO_RESOURCE_FOUND);
		}

		userLawRepository.deleteByUser_IdAndLaw_Id(userId, lawId);
	}
}
