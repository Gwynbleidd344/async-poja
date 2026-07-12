package async.poja.gwen.mail;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class PdfGenerator implements Function<String, File> {

  @Override
  public File apply(String htmlContent) {
    try {
      var pdfFile = File.createTempFile("document", ".pdf");
      try (OutputStream os = new FileOutputStream(pdfFile)) {
        var builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(os);
        builder.run();
      }
      return pdfFile;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
