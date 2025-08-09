package bookclub.devops.webapp.repository;

import bookclub.devops.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Search for users by username (for friend search)
    List<User> findByUsernameContainingIgnoreCase(String username);

    // Get friends of a user
    @Query("SELECT f.friend FROM Friendship f WHERE f.user.id = :userId")
    List<User> findFriendsByUserId(@Param("userId") Long userId);

    // Get friend count
    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.user.id = :userId")
    Long countFriendsByUserId(@Param("userId") Long userId);
}
