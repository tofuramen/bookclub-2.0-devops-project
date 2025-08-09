package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedService {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    // Get activity feed for user (posts and comments from friends)
    public List<Post> getFriendsRecentPosts(Long userId) {
        return postService.getFriendsRecentPosts(userId);
    }

    public List<Comment> getFriendsRecentComments(Long userId) {
        return commentService.getFriendsRecentComments(userId);
    }

    // TODO: Later we can combine posts and comments into a unified activity feed
    // For now, we'll handle them separately in the controller
}
