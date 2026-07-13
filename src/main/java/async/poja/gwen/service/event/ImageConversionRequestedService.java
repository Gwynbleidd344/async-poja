package async.poja.gwen.service.event;

import async.poja.gwen.endpoint.event.model.ImageConversionRequested;
import async.poja.gwen.file.bucket.BucketComponent;
import async.poja.gwen.image.GrayscaleConverter;
import async.poja.gwen.mail.Email;
import async.poja.gwen.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageConversionRequestedService implements Consumer<ImageConversionRequested> {

  private final BucketComponent bucketComponent;
  private final GrayscaleConverter grayscaleConverter;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(ImageConversionRequested event) {
    var originalFile = bucketComponent.download(event.getBucketKey());
    var bwFile = grayscaleConverter.apply(originalFile);

    var extension = getExtension(event.getNomFichier());
    var bwBucketKey = "images/bw/" + UUID.randomUUID() + "." + extension;
    bucketComponent.upload(bwFile, bwBucketKey);
    var presignedUrl = bucketComponent.presign(bwBucketKey, Duration.ofHours(1));

    var emailHtmlBody =
        "Bonjour,<br><br>"
            + "Voici le lien pour télécharger votre image <b>"
            + event.getNomFichier()
            + "</b> en noir et blanc (valable 1h) :<br>"
            + "<a href=\"" + presignedUrl + "\">Télécharger l'image</a>";

    var email =
        new Email(
            new InternetAddress(event.getEmail()),
            List.of(),
            List.of(),
            "Votre image en noir et blanc",
            emailHtmlBody,
            List.of());

    mailer.accept(email);
  }

  private String getExtension(String filename) {
    return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
  }
}