package in.neuw.resource.demo.exception.handlers;

import in.neuw.resource.demo.models.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Karanbir Singh on 04/21/2020
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        var headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        var errorDto = new ErrorDto(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity(errorDto, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ErrorDto> handleAccessDeniedException(AccessDeniedException ex) {
        var headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        var errorDto = new ErrorDto(ex.getMessage(), HttpStatus.FORBIDDEN);
        errorDto.setCode(103);
        return new ResponseEntity(errorDto, headers, HttpStatus.FORBIDDEN);
    }

    // for any other custom exceptions
    /*@ExceptionHandler(AppRuntimeException.class)
    protected ResponseEntity<ErrorDto> appRuntimeException(WebRequest request, AppRuntimeException ex) {
        var errorDto = new ErrorDto(ex.getMessage(), ex.getErrorCodes().getStatus());

        return new ResponseEntity(errorDto, ex.getErrorCodes().getStatus());
    }*/

}
