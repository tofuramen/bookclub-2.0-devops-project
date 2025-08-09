package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class UserSearchPageController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String q, Model model) {
        if (q != null && !q.trim().isEmpty()) {
            List<User> users = userService.searchUsers(q.trim());
            model.addAttribute("users", users);
            model.addAttribute("query", q);
        }
        return "users/search";
    }
}
