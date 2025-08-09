package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.service.UserService;
import bookclub.devops.webapp.service.FeedService;
import bookclub.devops.webapp.service.BookspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Optional;

@Controller
public class HomePageController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private BookspaceService bookspaceService;

    @GetMapping("/dashboard")
    public String homePage(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();

        // Get activity feed (recent posts from friends)
        List<Post> recentPosts = feedService.getFriendsRecentPosts(user.getId());

        // Get user's bookspaces count
        Long bookspacesCount = bookspaceService.countUserBookspaces(user.getId());

        // Get friend count
        Long friendCount = userService.getFriendCount(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("bookspacesCount", bookspacesCount);
        model.addAttribute("friendCount", friendCount);

        return "home/dashboard";
    }
}
