package be.kdg.kandoe.frontend.controller.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public int test() {
        return 1337;
    }
}
