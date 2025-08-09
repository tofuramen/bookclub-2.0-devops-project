package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.Post;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Create new post
    public Post createPost(String title, String content, User author, Bookspace bookspace) {
        Post post = new Post(title, content, author, bookspace);
        return postRepository.save(post);
    }

    // Find post by ID
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    // Get posts in a bookspace
    public List<Post> getBookspacePosts(Long bookspaceId) {
        return postRepository.findByBookspaceIdOrderByCreatedAtDesc(bookspaceId);
    }

    // Get posts by author
    public List<Post> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    // Get recent posts from friends (for activity feed)
    public List<Post> getFriendsRecentPosts(Long userId) {
        return postRepository.findRecentPostsByFriends(userId);
    }

    // Update post
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    // Delete post
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // Check if user is the author of the post
    public boolean isAuthor(Long postId, Long userId) {
        Optional<Post> post = findById(postId);
        return post.isPresent() && post.get().getAuthor().getId().equals(userId);
    }

    // Count posts in bookspace
    public Long countPostsInBookspace(Long bookspaceId) {
        return postRepository.countByBookspaceId(bookspaceId);
    }
}
