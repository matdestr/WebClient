package integrationtest.oauth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @RequestMapping()
    public String unAuthorizedTestMethod(){
        return "test";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/auth")
    public String authorizedTestMethod(){
        return "test";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/admin")
    public String adminTestMethod(){
        return "test";
    }
}
