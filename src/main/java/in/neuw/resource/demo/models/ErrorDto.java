package in.neuw.resource.demo.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author Karanbir Singh on 04/21/2020
 */
@Getter
@Setter
@Accessors(chain = true)
public class ErrorDto {

    private String message;
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    private int code;
    private String error;

    public ErrorDto(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public int getCode() {
        return status.value() * 1000 + code;
    }

}
