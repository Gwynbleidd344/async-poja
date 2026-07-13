package async.poja.gwen.service.event;

import async.poja.gwen.endpoint.event.model.SendEmailRequested;
import async.poja.gwen.file.bucket.BucketComponent;
import async.poja.gwen.mail.Email;
import async.poja.gwen.mail.Mailer;
import async.poja.gwen.mail.PdfGenerator;
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
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {

  private final Mailer mailer;
  private final PdfGenerator pdfGenerator;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {
    var pdfHtmlContent = "<html><body><p>PDF file succesfully created</p></body></html>";
    var pdfFile = pdfGenerator.apply(pdfHtmlContent);

    var bucketKey = "documents/" + UUID.randomUUID() + ".pdf";
    bucketComponent.upload(pdfFile, bucketKey);
    var presignedUrl = bucketComponent.presign(bucketKey, Duration.ofHours(24));

    var emailHtmlBody =
        "Cher " + event.getNom() + ",<br><br>"
            + "Voici le lien pour télécharger votre document (valable 24h) : <br>"
            + "<a href=\"" + presignedUrl + "\">Télécharger le PDF</a>";

    var email =
        new Email(
            new InternetAddress(event.getTo()),
            List.of(),
            List.of(),
            "Votre document est prêt",
            emailHtmlBody,
            List.of());

    mailer.accept(email);
  }
}