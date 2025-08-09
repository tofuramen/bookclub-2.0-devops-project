package bookclub.devops.webapp.repository;


import bookclub.devops.webapp.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by bookspace
    List<Post> findByBookspaceIdOrderByCreatedAtDesc(Long bookspaceId);

    // Find posts by author
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Get recent posts from friends (for activity feed)
    @Query("SELECT p FROM Post p JOIN Friendship f ON p.author.id = f.friend.id " +
            "WHERE f.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findRecentPostsByFriends(@Param("userId") Long userId);

    // Count posts in a bookspace
    Long countByBookspaceId(Long bookspaceId);
}
