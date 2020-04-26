package in.neuw.resource.demo.web.controllers;

import in.neuw.resource.demo.models.TestDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Karanbir Singh on 04/26/2020
 */
@RestController
@RequestMapping("/api/v1/")
public class TestController {

    @PreAuthorize("hasAuthority('SCOPE_system')")
    @GetMapping("test")
    public Mono<TestDto> getTest(@RequestParam(defaultValue = "test", required = false) String message) {
        return Mono.just(new TestDto().setMessage(message));
    }

}
