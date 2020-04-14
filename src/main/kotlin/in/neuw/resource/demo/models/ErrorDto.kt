package `in`.neuw.resource.demo.models

import org.springframework.http.HttpStatus

data class ErrorDto(val message: String?, val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) {

    var code: Int = this.status.value() * 1000 + 100
        get() = this.status.value() * 1000 + field

    var error: String = ""

}