package `in`.neuw.resource.demo.web.controllers

import `in`.neuw.resource.demo.models.TestDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/")
class TestController {

    @PreAuthorize("hasAuthority('SCOPE_system')")
    @GetMapping("test")
    fun getTest(@RequestParam(defaultValue = "test", required = false) message:String): Mono<TestDto> {
        return Mono.just(TestDto(message));
    }

}