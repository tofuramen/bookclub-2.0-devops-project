package bookclub.devops.webapp.controller;


import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.entity.Comment;
import bookclub.devops.webapp.service.UserService;
import bookclub.devops.webapp.service.PostService;
import bookclub.devops.webapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class PostDetailPageController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/posts/{id}")
    public String postDetailPage(@PathVariable Long id,
                                 Authentication authentication,
                                 Model model) {

        Optional<Post> postOpt = postService.findById(id);
        Optional<User> userOpt = userService.findByUsername(authentication.getName());

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/";
        }

        Post post = postOpt.get();
        User currentUser = userOpt.get();

        // Get comments for this post
        List<Comment> comments = commentService.getPostComments(id);

        // Check if current user is the author
        boolean isAuthor = postService.isAuthor(id, currentUser.getId());

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newComment", new Comment());

        return "posts/detail";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute("newComment") Comment comment,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model) {

        Optional<Post> postOpt = postService.findById(id);
        Optional<User> userOpt = userService.findByUsername(authentication.getName());

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            Post post = postOpt.get();
            User currentUser = userOpt.get();
            List<Comment> comments = commentService.getPostComments(id);
            boolean isAuthor = postService.isAuthor(id, currentUser.getId());

            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            model.addAttribute("isAuthor", isAuthor);
            model.addAttribute("currentUser", currentUser);

            return "posts/detail";
        }

        Post post = postOpt.get();
        User user = userOpt.get();

        commentService.createComment(comment.getContent(), user, post);

        return "redirect:/posts/" + id;
    }
}
