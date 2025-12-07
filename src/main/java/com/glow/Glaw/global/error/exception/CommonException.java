package com.glow.Glaw.global.error.exception;

import com.glow.Glaw.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException {
	private final ErrorCode errorCode;
}
