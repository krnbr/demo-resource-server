package `in`.neuw.resource.demo.models

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus

data class TestDto(var message: String = "") {

    @JsonIgnore
    var status:HttpStatus = HttpStatus.OK

    var code: Int = status.value()

}