package bookclub.devops.webapp.service;

import bookclub.devops.webapp.entity.Bookspace;
import bookclub.devops.webapp.entity.User;
import bookclub.devops.webapp.repository.BookspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookspaceService {

    @Autowired
    private BookspaceRepository bookspaceRepository;

    // Create new bookspace
    public Bookspace createBookspace(String title, String description, User owner) {
        Bookspace bookspace = new Bookspace(title, description, owner);
        return bookspaceRepository.save(bookspace);
    }

    // Find bookspace by ID
    public Optional<Bookspace> findById(Long id) {
        return bookspaceRepository.findById(id);
    }

    // Get user's bookspaces
    public List<Bookspace> getUserBookspaces(Long userId) {
        return bookspaceRepository.findByOwnerIdOrderByUpdatedAtDesc(userId);
    }

    // Get user's bookspaces by username
    public List<Bookspace> getUserBookspacesByUsername(String username) {
        return bookspaceRepository.findByOwnerUsernameOrderByUpdatedAtDesc(username);
    }

    // Update bookspace
    public Bookspace updateBookspace(Bookspace bookspace) {
        return bookspaceRepository.save(bookspace);
    }

    // Delete bookspace
    public void deleteBookspace(Long id) {
        bookspaceRepository.deleteById(id);
    }

    // Check if user owns bookspace
    public boolean isOwner(Long bookspaceId, Long userId) {
        Optional<Bookspace> bookspace = findById(bookspaceId);
        return bookspace.isPresent() && bookspace.get().getOwner().getId().equals(userId);
    }

    // Count bookspaces by owner
    public Long countUserBookspaces(Long userId) {
        return bookspaceRepository.countByOwnerId(userId);
    }
}
