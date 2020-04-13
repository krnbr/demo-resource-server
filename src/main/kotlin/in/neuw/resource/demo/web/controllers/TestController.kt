package `in`.neuw.resource.demo.web.controllers

import `in`.neuw.resource.demo.models.TestDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class TestController {

    @GetMapping("test")
    fun getTest(@RequestParam(defaultValue = "test", required = false) message:String):TestDto {
        return TestDto(message);
    }

}