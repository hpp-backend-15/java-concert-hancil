package io.hhplus.javaconcerthancil.interfaces.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    E001("E001", "대기열 상태가 활성상태가 아닙니다."),
    E002("E002", "좌석이 이미 예약 되어 있습니다."),
    E003("E003", "유효하지 않은 토큰입니다."),
    E005("E005", "결제 잔액이 부족합니다."),

    E404("E404", "데이터를 조회할 수 없습니다."),
    E500("E500", "알 수 없는 에러입니다. 관리자한테 문의해주세요."),
    ;

    private final String code;
    private final String message;

}
