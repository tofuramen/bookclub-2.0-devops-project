package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.service.BookspaceService;
import bookclub.devops.webapp.service.PostService;
import bookclub.devops.webapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class CreatePostPageController {

    private final UserService userService;

    private final BookspaceService bookspaceService;

    private final PostService postService;

    public CreatePostPageController(UserService userService, BookspaceService bookspaceService, PostService postService) {
        this.userService = userService;
        this.bookspaceService = bookspaceService;
        this.postService = postService;
    }

    @GetMapping("/bookspaces/{bookspaceId}/posts/new")
    public String createPostPage(@PathVariable Long bookspaceId,
                                 Authentication authentication,
                                 Model model) {

        Optional<Bookspace> bookspaceOpt = bookspaceService.findById(bookspaceId);
        Optional<User> userOpt = userService.findByUsername(authentication.getName());

        if (bookspaceOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/bookspaces";
        }

        Bookspace bookspace = bookspaceOpt.get();
        User user = userOpt.get();

        // Check if user owns this bookspace
        if (!bookspaceService.isOwner(bookspaceId, user.getId())) {
            return "redirect:/bookspaces/" + bookspaceId;
        }

        model.addAttribute("post", new Post());
        model.addAttribute("bookspace", bookspace);

        return "posts/create";
    }

    @PostMapping("/bookspaces/{bookspaceId}/posts/new")
    public String createPost(@PathVariable Long bookspaceId,
                             @Valid @ModelAttribute Post post,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model) {

        if (bindingResult.hasErrors()) {
            Optional<Bookspace> bookspaceOpt = bookspaceService.findById(bookspaceId);
            if (bookspaceOpt.isPresent()) {
                model.addAttribute("bookspace", bookspaceOpt.get());
            }
            return "posts/create";
        }

        Optional<Bookspace> bookspaceOpt = bookspaceService.findById(bookspaceId);
        Optional<User> userOpt = userService.findByUsername(authentication.getName());

        if (bookspaceOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/bookspaces";
        }

        Bookspace bookspace = bookspaceOpt.get();
        User user = userOpt.get();

        // Check ownership
        if (!bookspaceService.isOwner(bookspaceId, user.getId())) {
            return "redirect:/bookspaces/" + bookspaceId;
        }

        postService.createPost(post.getTitle(), post.getContent(), user, bookspace);

        return "redirect:/bookspaces/" + bookspaceId;
    }
}
