package com.ulbra.metroplanpdfautomation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@SpringBootTest
class MetroplanPdfAutomationApplicationTests {

  @Test
  void contextLoads() throws IOException {
    PDDocument pdDocument=PDDocument.load(new File("enrollmentValidation.pdf"));
    PDFRenderer pdfRenderer=new PDFRenderer(pdDocument);
    BufferedImage bufferedImage=pdfRenderer.renderImageWithDPI(0, 300);
    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
    var value=Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    System.out.println(value);
  }
}
