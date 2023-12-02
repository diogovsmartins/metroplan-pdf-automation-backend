package com.ulbra.metroplanpdfautomation.services;

import static com.ulbra.metroplanpdfautomation.helpers.ConstantHelper.PRESENCIALITY_DECLARATION_TEXT_TEMPLATE;
import static com.ulbra.metroplanpdfautomation.helpers.ConstantHelper.PRESENCIALITY_DECLARATION_TITTLE;

import com.ulbra.metroplanpdfautomation.domain.DTOs.StudentInformation;
import com.ulbra.metroplanpdfautomation.domain.entities.PdfEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfEditorService {
  private final PdfEntityService pdfEntityService;
  Logger LOGGER = LoggerFactory.getLogger(PdfEditorService.class);

  @Autowired
  public PdfEditorService(final PdfEntityService pdfEntityService) {
    this.pdfEntityService = pdfEntityService;
  }

  // this method calls the utility methods provided in this class to retrieve the pdf we received in
  // the response
  // write the needed text to the new PDF and then merge both
  public void generatePresencialityDeclaration(
      final HttpServletResponse response,
      final StudentInformation studentInformation,
      final MultipartFile multipartFile) {
    AtomicReference<Integer> lineIndexList = new AtomicReference<>(0);
    // Extract MultipartFile to PDFBox document
    PDDocument pdDocument = extractTextFromMultipartFile(multipartFile);
    if (Objects.isNull(pdDocument)) {
      return;
    }

    try (PDDocument presencialityDeclarationPdf = new PDDocument()) {
      presencialityDeclarationPdf.addPage(new PDPage());
      float pageWidth = presencialityDeclarationPdf.getPage(0).getMediaBox().getWidth();
      PDPageContentStream contentStream =
          new PDPageContentStream(
              presencialityDeclarationPdf, presencialityDeclarationPdf.getPage(0));
      contentStream.setLeading(14.5f);
      String specificPresencialityDeclarationText =
          String.format(
              PRESENCIALITY_DECLARATION_TEXT_TEMPLATE,
              studentInformation.getName(),
              studentInformation);

      contentStream.drawImage(
          PDImageXObject.createFromFile("image.png", presencialityDeclarationPdf), 20, 650);
      writeText(contentStream, pageWidth, PRESENCIALITY_DECLARATION_TITTLE, 24, 88, lineIndexList);
      writeText(
          contentStream, pageWidth, specificPresencialityDeclarationText, 16, 35, lineIndexList);
      contentStream.close();
      PDDocument mergedDocument = mergeDocuments(pdDocument, presencialityDeclarationPdf);
      mergedDocument.save(response.getOutputStream());
      saveGeneratedPdf(mergeDocuments(pdDocument, presencialityDeclarationPdf), studentInformation);
      pdDocument.close();
    } catch (Exception e) {
      LOGGER.error("Application threw an IOException when generating the desired PDF.");
    }
  }

  private static PDDocument mergeDocuments(final PDDocument file, final PDDocument pdDocument1) {
    PDDocument mergedDocument = new PDDocument();
    mergedDocument.addPage(file.getPage(0));
    mergedDocument.addPage(pdDocument1.getPage(0));
    return mergedDocument;
  }

  @Async
  public void saveGeneratedPdf(
      final PDDocument pdDocument, final StudentInformation studentInformation) {
    try {
      var startTime = System.currentTimeMillis();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

      ImageIO.write(pdfRenderer.renderImage(1, 1), "PNG", baos);
      var imageByteArray = Base64.getEncoder().encodeToString(baos.toByteArray());
      baos.reset();
      pdDocument.save(baos);

      pdfEntityService.savePdf(baos.toByteArray(), studentInformation, imageByteArray);

      long endTime = startTime - System.currentTimeMillis();
      System.out.println("This operation took:" + (endTime));
    } catch (IOException ioException) {
      System.out.println("Validation");
    }
  }

  public void SignDocument(final List<PdfEntity> pdfEntityList) {
    pdfEntityList.stream()
        .forEach(
            pdfEntity -> {
              try {
                PDDocument pdDocument = PDDocument.load(pdfEntity.getPdfBytes());
                PDPageContentStream contentStream =
                    new PDPageContentStream(pdDocument, pdDocument.getPage(0));
                contentStream.drawImage(
                    PDImageXObject.createFromFile("image.png", pdDocument), 20, 650);
                contentStream.close();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pdDocument.save(baos);
                pdfEntityService.updatePdf(baos.toByteArray(), pdfEntity);
              } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
              }
            });
  }

  // This method is used to write text to the PDF, it receives some of the variables needed to split
  // the text
  // into smaller portions like pageWidth, fontSize and margin. After the text has been splitted
  // we do a foreach to iterate and write the text to the given PDF, if something goes wrong we log
  // the error
  private void writeText(
      final PDPageContentStream contentStream,
      float pageWidth,
      final String textToWrite,
      final Integer fontSize,
      final Integer margin,
      final AtomicReference<Integer> lineIndexList)
      throws IOException {
    contentStream.setFont(PDType1Font.COURIER, fontSize);
    List<String> splitText = splitTextIntoWritableParts(textToWrite, pageWidth, fontSize, margin);
    List<Integer> linesList = List.of(550, 450, 350, 250, 150);

    try {
      contentStream.beginText();
      contentStream.newLineAtOffset(margin, linesList.get(lineIndexList.get()));
      splitText.stream()
          .forEach(
              textPiece -> {
                try {
                  contentStream.showText(textPiece);
                  contentStream.newLine();
                } catch (IOException ioException) {
                  LOGGER.error("error");
                }
              });
      lineIndexList.getAndSet(lineIndexList.get() + 1);
      contentStream.endText();
    } catch (Exception ex) {
      LOGGER.error("Error when writing text to PDF.");
    }
  }

  // This method receives the MultipartFile, uses a standard InputStream to transform that file
  // into a PDFBoxDocument and returns the pdf, makes a log if an IoException is thrown
  public PDDocument extractTextFromMultipartFile(MultipartFile multipartFile) {
    try (InputStream inputStream = multipartFile.getInputStream()) {
      return PDDocument.load(inputStream);
    } catch (IOException ioException) {
      LOGGER.error("Error reading MultipartFile, error: {}", ioException.getMessage());
    }
    return null;
  }

  // This method takes a long text and splits it into smaller pieces that can be writed inside the
  // pdf
  // given the page width provided, fontsize and margin, some of these values may need to be tweaked
  // a little
  // in case the page size changes
  public List<String> splitTextIntoWritableParts(
      String text, double pageWidth, int fontSize, double margin) {
    // Calculate the average characters per line
    double charactersPerLine = (pageWidth - 2 * margin) / (fontSize * 0.6);

    // Initialize variables
    List<String> textPieces = new ArrayList<>();
    StringBuilder currentPiece = new StringBuilder();
    int characterCount = 0;

    // Split the text into words
    String[] words = text.split(" ");

    for (String word : words) {
      // Check if adding the word exceeds the characters per line
      if (characterCount + word.length() + 1 <= charactersPerLine) {
        if (!currentPiece.toString().isEmpty()) {
          currentPiece.append(" ").append(word);
        } else {
          currentPiece.append(word);
        }
        characterCount += word.length() + 1;
      } else {
        textPieces.add(currentPiece.toString());
        currentPiece.setLength(0);
        currentPiece.append(word);
        characterCount = word.length();
      }
    }

    if (!currentPiece.toString().isEmpty()) {
      textPieces.add(currentPiece.toString());
    }

    return textPieces;
  }
}
