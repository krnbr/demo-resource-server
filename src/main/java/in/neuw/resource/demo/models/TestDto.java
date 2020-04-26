package in.neuw.resource.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author Karanbir Singh on 04/26/2020
 */
@Getter
@Setter
@Accessors(chain = true)
public class TestDto {

    private String message;

    @JsonIgnore
    private HttpStatus status;

    private int code;

}
