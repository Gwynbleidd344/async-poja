package async.poja.gwen.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageSubmission {

  @Id private UUID id;

  @Column(name = "nom_fichier", nullable = false)
  private String nomFichier;

  @Column(nullable = false)
  private String email;
}