package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.repository.UserRepository;
import bookclub.devops.webapp.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // Search users by username
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
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
