package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.repository.FriendshipRepository;
import bookclub.devops.webapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, FriendshipRepository friendshipRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // User registration
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Update user profile
    public User updateProfile(User user) {
        return userRepository.save(user);
    }

    // Search users by username or email
    public List<User> searchUsers(String query) {
        // Search by both username and email, combine results and remove duplicates
        List<User> usersByUsername = userRepository.findByUsernameContainingIgnoreCase(query);
        List<User> usersByEmail = userRepository.findByEmailContainingIgnoreCase(query);

        // Combine and deduplicate
        Set<User> uniqueUsers = new HashSet<>(usersByUsername);
        uniqueUsers.addAll(usersByEmail);

        return new ArrayList<>(uniqueUsers);
    }

    // Get user's friends
    public List<User> getUserFriends(Long userId) {
        return userRepository.findFriendsByUserId(userId);
    }

    // Get friend count
    public Long getFriendCount(Long userId) {
        return userRepository.countFriendsByUserId(userId);
    }

    // Check if users are friends
    public boolean areFriends(Long userId, Long friendId) {
        return friendshipRepository.existsByUserIdAndFriendId(userId, friendId);
    }
}
