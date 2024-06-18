package zzibu.jeho.tagify.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status : HttpStatus, val message : String) {
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "허용된 파일 크기를 초과합니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 포맷입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에 문제가 발생했습니다.");
}