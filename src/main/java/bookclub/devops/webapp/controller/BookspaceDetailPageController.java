package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.service.BookspaceService;
import bookclub.devops.webapp.service.PostService;
import bookclub.devops.webapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class BookspaceDetailPageController {

    private final UserService userService;

    private final BookspaceService bookspaceService;

    private final PostService postService;

    public BookspaceDetailPageController(UserService userService, BookspaceService bookspaceService, PostService postService) {
        this.userService = userService;
        this.bookspaceService = bookspaceService;
        this.postService = postService;
    }

    @GetMapping("/bookspaces/{id}")
    public String bookspaceDetailPage(@PathVariable Long id,
                                      Authentication authentication,
                                      Model model) {

        Optional<Bookspace> bookspaceOpt = bookspaceService.findById(id);
        if (bookspaceOpt.isEmpty()) {
            return "redirect:/bookspaces";
        }

        Bookspace bookspace = bookspaceOpt.get();
        Optional<User> currentUserOpt = userService.findByUsername(authentication.getName());

        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User currentUser = currentUserOpt.get();

        // Get posts in this bookspace
        List<Post> posts = postService.getBookspacePosts(id);

        // Check if current user owns this bookspace
        boolean isOwner = bookspaceService.isOwner(id, currentUser.getId());

        model.addAttribute("bookspace", bookspace);
        model.addAttribute("posts", posts);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentUser", currentUser);

        return "bookspaces/detail";
    }
}
