package com.berryweb.shop.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR("C001", "내부 서버 오류가 발생했습니다."),
    INVALID_INPUT("C002", "잘못된 입력값입니다."),
    UNAUTHORIZED("C003", "인증이 필요합니다."),
    ACCESS_DENIED("C004", "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),

    // Shop
    SHOP_NOT_FOUND("S001", "상점을 찾을 수 없습니다."),

    // Category
    CATEGORY_NOT_FOUND("CT001", "카테고리를 찾을 수 없습니다."),

    // Product
    PRODUCT_NOT_FOUND("P001", "상품을 찾을 수 없습니다."),
    DUPLICATE_SLUG("P002", "이미 사용중인 슬러그입니다."),

    // Review
    REVIEW_NOT_FOUND("R001", "리뷰를 찾을 수 없습니다."),
    DUPLICATE_REVIEW("R002", "이미 리뷰를 작성하셨습니다."),

    // File
    FILE_NOT_FOUND("F001", "파일을 찾을 수 없습니다."),
    FILE_SIZE_EXCEEDED("F002", "파일 크기가 제한을 초과했습니다."),
    FILE_COUNT_EXCEEDED("F003", "파일 개수가 제한을 초과했습니다."),
    FILE_EXTENSION_NOT_ALLOWED("F004", "허용되지 않는 파일 형식입니다."),
    FILE_SAVE_FAILED("F005", "파일 저장에 실패했습니다."),
    FILE_DOWNLOAD_FAILED("F006", "파일 다운로드에 실패했습니다."),
    FILE_DELETE_FAILED("F007", "파일 삭제에 실패했습니다.");

    private final String code;
    private final String message;

}
