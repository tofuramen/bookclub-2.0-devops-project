package bookclub.devops.webapp.controller;


import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.service.UserService;
import bookclub.devops.webapp.service.BookspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Optional;

@Controller
public class BookspaceListPageController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookspaceService bookspaceService;

    @GetMapping("/bookspaces")
    public String bookspaceListPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        List<Bookspace> bookspaces = bookspaceService.getUserBookspaces(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("bookspaces", bookspaces);

        return "bookspaces/list";
    }
}
