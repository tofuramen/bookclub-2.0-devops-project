package bookclub.devops.webapp.repository;

import bookclub.devops.webapp.entity.Bookspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookspaceRepository extends JpaRepository<Bookspace, Long> {

    // Find bookspaces by owner
    List<Bookspace> findByOwnerIdOrderByUpdatedAtDesc(Long ownerId);

    // Find bookspaces by owner username
    List<Bookspace> findByOwnerUsernameOrderByUpdatedAtDesc(String username);

    // Count bookspaces by owner
    Long countByOwnerId(Long ownerId);
}
