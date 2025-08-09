package bookclub.devops.webapp.repository;

import bookclub.devops.webapp.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Check if friendship exists between two users
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    // Find friendship between two users
    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);

    // Get all friendships for a user
    List<Friendship> findByUserId(Long userId);

    // Delete friendship (for unfriending)
    void deleteByUserIdAndFriendId(Long userId, Long friendId);

    // Get mutual friends count
    @Query("SELECT COUNT(f1) FROM Friendship f1 JOIN Friendship f2 ON f1.friend.id = f2.friend.id " +
            "WHERE f1.user.id = :userId1 AND f2.user.id = :userId2")
    Long countMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
