package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.service.BookspaceService;
import bookclub.devops.webapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class CreateBookspacePageController {

    private final UserService userService;

    private final BookspaceService bookspaceService;

    public CreateBookspacePageController(UserService userService, BookspaceService bookspaceService) {
        this.userService = userService;
        this.bookspaceService = bookspaceService;
    }

    @GetMapping("/bookspaces/new")
    public String createBookspacePage(Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("bookspace", new Bookspace());
        model.addAttribute("currentUser", userOpt.get());
        return "bookspaces/create";
    }

    @PostMapping("/bookspaces/new")
    public String createBookspace(@Valid @ModelAttribute Bookspace bookspace,
                                  BindingResult bindingResult,
                                  Authentication authentication,
                                  Model model) {

        if (bindingResult.hasErrors()) {
            return "bookspaces/create";
        }

        Optional<User> userOpt = userService.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();
        Bookspace savedBookspace = bookspaceService.createBookspace(
                bookspace.getTitle(),
                bookspace.getDescription(),
                user
        );

        return "redirect:/bookspaces/" + savedBookspace.getId();
    }
}
