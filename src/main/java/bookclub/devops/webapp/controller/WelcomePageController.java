package bookclub.devops.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomePageController {

    @GetMapping("/")
    public String welcomePage() {
        return "welcome";
    }

    @GetMapping("/home")
    public String homePage() {
        return "redirect:/"; // This will go to HomePageController after login
    }
}
