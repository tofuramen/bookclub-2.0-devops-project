package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.service.UserService;
import bookclub.devops.webapp.service.BookspaceService;
import bookclub.devops.webapp.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import java.util.Optional;

@Controller
public class UserProfilePageController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookspaceService bookspaceService;

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/users/{username}")
    public String userProfilePage(@PathVariable String username, Authentication authentication, Model model) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "redirect:/";
        }

        User profileUser = userOpt.get();
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);

        // Get user's bookspaces
        List<Bookspace> bookspaces = bookspaceService.getUserBookspacesByUsername(username);

        // Get friend count
        Long friendCount = userService.getFriendCount(profileUser.getId());

        // Check if current user is friends with profile user
        boolean isFriend = currentUser != null &&
                friendshipService.areFriends(currentUser.getId(), profileUser.getId());

        // Check if viewing own profile
        boolean isOwnProfile = currentUser != null &&
                currentUser.getId().equals(profileUser.getId());

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("bookspaces", bookspaces);
        model.addAttribute("friendCount", friendCount);
        model.addAttribute("isFriend", isFriend);
        model.addAttribute("isOwnProfile", isOwnProfile);

        return "users/profile";
    }

    @PostMapping("/users/{username}/add-friend")
    public String addFriend(@PathVariable String username, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        User friend = userService.findByUsername(username).orElse(null);

        if (currentUser != null && friend != null) {
            friendshipService.addFriend(currentUser, friend);
        }

        return "redirect:/users/" + username;
    }

    @PostMapping("/users/{username}/remove-friend")
    public String removeFriend(@PathVariable String username, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        User friend = userService.findByUsername(username).orElse(null);

        if (currentUser != null && friend != null) {
            friendshipService.removeFriend(currentUser, friend);
        }

        return "redirect:/users/" + username;
    }
}
