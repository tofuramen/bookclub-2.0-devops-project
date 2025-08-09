package bookclub.devops.webapp.repository;

import bookclub.devops.webapp.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by post
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // Find comments by author
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Count comments on a post
    Long countByPostId(Long postId);

    // Get recent comments from friends (for activity feed)
    @Query("SELECT c FROM Comment c JOIN Friendship f ON c.author.id = f.friend.id " +
            "WHERE f.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByFriends(@Param("userId") Long userId);
}
