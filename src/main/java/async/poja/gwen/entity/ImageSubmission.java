package async.poja.gwen.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "image_submission")
@Getter
@Setter
@NoArgsConstructor
public class ImageSubmission {

  @Id private UUID id;

  @Column(name = "nom_fichier", nullable = false)
  private String nomFichier;

  @Column(nullable = false)
  private String email;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public ImageSubmission(UUID id, String nomFichier, String email) {
    this.id = id;
    this.nomFichier = nomFichier;
    this.email = email;
  }
}
