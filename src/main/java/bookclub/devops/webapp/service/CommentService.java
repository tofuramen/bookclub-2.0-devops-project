package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.Comment;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // Create new comment
    public Comment createComment(String content, User author, Post post) {
        Comment comment = new Comment(content, author, post);
        return commentRepository.save(comment);
    }

    // Find comment by ID
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    // Get comments for a post
    public List<Comment> getPostComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    // Get comments by author
    public List<Comment> getCommentsByAuthor(Long authorId) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    // Get recent comments from friends (for activity feed)
    public List<Comment> getFriendsRecentComments(Long userId) {
        return commentRepository.findRecentCommentsByFriends(userId);
    }

    // Update comment
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Delete comment
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    // Check if user is the author of the comment
    public boolean isAuthor(Long commentId, Long userId) {
        Optional<Comment> comment = findById(commentId);
        return comment.isPresent() && comment.get().getAuthor().getId().equals(userId);
    }

    // Count comments on a post
    public Long countCommentsOnPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
