package be.kdg.kandoe.frontend.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * Home controller to show the index file
 */

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }
}
