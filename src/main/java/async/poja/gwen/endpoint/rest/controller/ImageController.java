package async.poja.gwen.endpoint.rest.controller;

import async.poja.gwen.endpoint.event.EventProducer;
import async.poja.gwen.endpoint.event.model.ImageConversionRequested;
import async.poja.gwen.entity.ImageSubmission;
import async.poja.gwen.file.bucket.BucketComponent;
import async.poja.gwen.repository.ImageSubmissionRepository;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
public class ImageController {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

  private final ImageSubmissionRepository imageSubmissionRepository;
  private final EventProducer<ImageConversionRequested> eventProducer;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  @PostMapping
  public ResponseEntity<ImageSubmission> submitImage(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {

    var originalFilename = file.getOriginalFilename();
    var extension = getExtension(originalFilename);
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Seuls les fichiers jpg/jpeg/png sont acceptés");
    }

    var id = UUID.randomUUID();

    var imageSubmission = new ImageSubmission(id, originalFilename, email);
    imageSubmissionRepository.save(imageSubmission);

    var tempFile = File.createTempFile("original-", "." + extension);
    file.transferTo(tempFile);
    var bucketKey = "images/original/" + id + "." + extension;
    bucketComponent.upload(tempFile, bucketKey);

    var event =
        ImageConversionRequested.builder()
            .imageId(id)
            .bucketKey(bucketKey)
            .nomFichier(originalFilename)
            .email(email)
            .build();
    eventProducer.accept(List.of(event));

    return ResponseEntity.status(HttpStatus.CREATED).body(imageSubmission);
  }

  @GetMapping
  public List<ImageSubmission> findAll() {
    return imageSubmissionRepository.findAll();
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nom de fichier invalide");
    }
    return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
  }
}
