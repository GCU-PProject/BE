package com.glow.Glaw.global.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.glow.Glaw.global.error.ErrorCode;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private int code;
	private LocalDateTime timestamp;
	private String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> error;

	@Builder
	public ApiResponse(LocalDateTime timestamp, String message, boolean success, int code, T result,
		Map<String, String> error) {
		this.timestamp = timestamp;
		this.success = success;
		this.message = message;
		this.code = code;
		this.result = result;
		this.error = error;
	}

	// 201 Create 성공 응답
	public static <T> ApiResponse<T> success(T result, int code) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.timestamp(now)
			.success(true)
			.message("성공")
			.code(code)
			.result(result)
			.error(null)
			.build();
	}

	// 메시지를 받는 성공 응답 메소드 추가
	public static <T> ApiResponse<T> success(String message, T result) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.timestamp(now)
			.success(true)
			.message(message)  // 커스텀 메시지 사용
			.code(200)
			.result(result)
			.error(null)
			.build();
	}

	// 성공 응답 200
	public static <T> ApiResponse<T> success(T result) {
		return success(result, 200);
	}

	//성공 응답 result 없는 버전
	public static <T> ApiResponse<T> success() {
		return success(null, 200);
	}

	//실패 응답 (단일 필드)
	public static <T> ApiResponse<T> fail(ErrorCode code, String field, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(field, message);
		return fail(code, errorMap);
	}

	// 실패 응답 (필드 별로)
	public static <T> ApiResponse<T> fail(ErrorCode code, Map<String, String> errorMap) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.timestamp(now)
			.success(false)
			.message(code.getMessage())
			.code(code.getStatus())
			.result(null)
			.error(errorMap)
			.build();
	}

	// 실패 응답 (에러 내용 있는 경우)
	public static <T> ApiResponse<T> fail(ErrorCode code, String message) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(false)
			.code(code.getStatus())
			.timestamp(now)
			.message(message)
			.result(null)
			.error(null)
			.build();
	}

	// 실패 응답 (에러 내용 없는 경우)
	public static <T> ApiResponse<T> fail(ErrorCode code) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(false)
			.code(code.getStatus())
			.timestamp(now)
			.message(code.getMessage())
			.result(null)
			.error(null)
			.build();
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper.writeValueAsString(this);
	}
}
