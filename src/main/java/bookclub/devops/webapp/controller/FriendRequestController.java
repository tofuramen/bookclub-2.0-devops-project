package bookclub.devops.webapp.controller;

import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.service.FriendshipService;
import bookclub.devops.webapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/friends")
public class FriendRequestController {

    private final FriendshipService friendshipService;
    private final UserService userService;

    public FriendRequestController(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequestDto request,
                                               Authentication auth) {
        try {
            // Get current user
            Optional<User> currentUserOpt = userService.findByUsername(auth.getName());
            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found"));
            }

            // Get target user
            Optional<User> targetUserOpt = userService.findById(request.getUserId());
            if (targetUserOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Target user not found"));
            }

            User currentUser = currentUserOpt.get();
            User targetUser = targetUserOpt.get();

            // Check if trying to add themselves
            if (currentUser.getId().equals(targetUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Cannot add yourself as friend"));
            }

            // Check if already friends
            if (friendshipService.areFriends(currentUser.getId(), targetUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Already friends with this user"));
            }

            // Add friend
            friendshipService.addFriend(currentUser, targetUser);

            return ResponseEntity.ok(new ApiResponse(true, "Friend added successfully"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Failed to add friend: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId,
                                          Authentication auth) {
        try {
            Optional<User> currentUserOpt = userService.findByUsername(auth.getName());
            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "User not found"));
            }

            Optional<User> friendOpt = userService.findById(friendId);
            if (friendOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Friend not found"));
            }

            User currentUser = currentUserOpt.get();
            User friend = friendOpt.get();

            // Remove friendship
            friendshipService.removeFriend(currentUser, friend);

            return ResponseEntity.ok(new ApiResponse(true, "Friend removed successfully"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Failed to remove friend: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<FriendStatusDto> getFriendshipStatus(@PathVariable Long userId,
                                                               Authentication auth) {
        try {
            Optional<User> currentUserOpt = userService.findByUsername(auth.getName());
            if (currentUserOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User currentUser = currentUserOpt.get();
            boolean areFriends = friendshipService.areFriends(currentUser.getId(), userId);

            return ResponseEntity.ok(new FriendStatusDto(areFriends));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO classes
    public static class FriendRequestDto {
        private Long userId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class FriendStatusDto {
        private boolean areFriends;

        public FriendStatusDto(boolean areFriends) {
            this.areFriends = areFriends;
        }

        public boolean isAreFriends() { return areFriends; }
        public void setAreFriends(boolean areFriends) { this.areFriends = areFriends; }
    }
}
