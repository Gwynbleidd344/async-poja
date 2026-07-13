package async.poja.gwen.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Function;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class GrayscaleConverter implements Function<File, File> {

  @Override
  public File apply(File inputFile) {
    try {
      var originalImage = ImageIO.read(inputFile);
      var grayImage =
          new BufferedImage(
              originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      var graphics = grayImage.getGraphics();
      graphics.drawImage(originalImage, 0, 0, null);
      graphics.dispose();

      var extension = getExtension(inputFile.getName());
      var outputFile = File.createTempFile("bw-", "." + extension);
      ImageIO.write(grayImage, extension, outputFile);
      return outputFile;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getExtension(String filename) {
    return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
  }
}