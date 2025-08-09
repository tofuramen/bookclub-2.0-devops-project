package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.Friendship;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    // Add friend (creates bidirectional relationship)
    @Transactional
    public void addFriend(User user, User friend) {
        if (!friendshipRepository.existsByUserIdAndFriendId(user.getId(), friend.getId())) {
            // Create both directions of friendship
            Friendship friendship1 = new Friendship(user, friend);
            Friendship friendship2 = new Friendship(friend, user);

            friendshipRepository.save(friendship1);
            friendshipRepository.save(friendship2);
        }
    }

    // Remove friend (removes bidirectional relationship)
    @Transactional
    public void removeFriend(User user, User friend) {
        friendshipRepository.deleteByUserIdAndFriendId(user.getId(), friend.getId());
        friendshipRepository.deleteByUserIdAndFriendId(friend.getId(), user.getId());
    }

    // Check if users are friends
    public boolean areFriends(Long userId, Long friendId) {
        return friendshipRepository.existsByUserIdAndFriendId(userId, friendId);
    }

    // Get user's friendships
    public List<Friendship> getUserFriendships(Long userId) {
        return friendshipRepository.findByUserId(userId);
    }

    // Get mutual friends count
    public Long getMutualFriendsCount(Long userId1, Long userId2) {
        return friendshipRepository.countMutualFriends(userId1, userId2);
    }
}
