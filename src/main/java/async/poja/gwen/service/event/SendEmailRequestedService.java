package async.poja.gwen.service.event;

import async.poja.gwen.endpoint.event.model.SendEmailRequested;
import async.poja.gwen.mail.Email;
import async.poja.gwen.mail.Mailer;
import async.poja.gwen.mail.PdfGenerator;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {

  private final Mailer mailer;
  private final PdfGenerator pdfGenerator;

  @SneakyThrows
  @Override
  public void accept(SendEmailRequested event) {
    var pdfHtmlContent = "<html><body><p>PDF file succesfully created</p></body></html>";
    var pdfFile = pdfGenerator.apply(pdfHtmlContent);

    var emailHtmlBody = "Cher " + event.getNom() + ",";

    var email =
        new Email(
            new InternetAddress(event.getTo()),
            List.of(),
            List.of(),
            "Votre document est prêt",
            emailHtmlBody,
            List.of(pdfFile));

    mailer.accept(email);
  }
}
