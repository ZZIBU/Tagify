package zzibu.jeho.tagify.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(ex: MaxUploadSizeExceededException, request: HttpServletRequest) : ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            ErrorCode.FILE_TOO_LARGE,
            ex.message ?: "파일 크기를 확인해주세요.",
            System.currentTimeMillis().toLocalDateTime(),
            request.requestURI,
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidFileTypeException::class)
    fun handleMultipartException(ex: InvalidFileTypeException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            ErrorCode.INVALID_FILE_TYPE,
            ex.message ?: "파일 타입을 확인해주세요.",
            System.currentTimeMillis().toLocalDateTime(),
            request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    private fun Long.toLocalDateTime(): String {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            ZoneOffset.systemDefault()
        ).format(DateTimeFormatter.ISO_DATE)
    }

}