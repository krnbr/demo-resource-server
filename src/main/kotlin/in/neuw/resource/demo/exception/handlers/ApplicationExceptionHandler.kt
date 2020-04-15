package `in`.neuw.resource.demo.exception.handlers

import `in`.neuw.resource.demo.models.ErrorDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.security.access.AccessDeniedException

@RestControllerAdvice
class ApplicationExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    fun handleException(ex: Exception): ResponseEntity<ErrorDto> {
        val headers = HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        val errorDto = ErrorDto(ex.message, HttpStatus.BAD_REQUEST)
        return ResponseEntity(errorDto, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = [AccessDeniedException::class])
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorDto> {
        val headers = HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        val errorDto = ErrorDto(ex.message, HttpStatus.FORBIDDEN)
        errorDto.code = 103
        return ResponseEntity(errorDto, headers, HttpStatus.FORBIDDEN);
    }

}