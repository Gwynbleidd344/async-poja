package async.poja.gwen.repository;

import async.poja.gwen.entity.ImageSubmission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageSubmissionRepository extends JpaRepository<ImageSubmission, UUID> {}
