package com.ulbra.metroplanpdfautomation.services;

import com.ulbra.metroplanpdfautomation.domain.DTOs.StudentInformation;
import com.ulbra.metroplanpdfautomation.domain.entities.PdfEntity;
import com.ulbra.metroplanpdfautomation.repositories.PdfRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfEntityService {

  private final PdfRepository pdfRepository;

  @Autowired
  public PdfEntityService(final PdfRepository pdfRepository) {
    this.pdfRepository = pdfRepository;
  }

  public void savePdf(
      final byte[] bytes, final StudentInformation studentInformation, final String base64Image) {

    pdfRepository.save(
        PdfEntity.builder()
            .pdfBytes(bytes)
            .userName(studentInformation.getName())
            .userEmail(studentInformation.getEmail())
            .base64Image(base64Image)
            .build());
  }

  public void updatePdf(final byte[] bytes, final PdfEntity pdfEntity) {
    pdfEntity.setPdfBytes(bytes);
    pdfRepository.save(pdfEntity);
  }

  public void getPdfById(final Long id, final HttpServletResponse response) {
    try {
      var pdf = pdfRepository.findById(id);
      if (pdf.isEmpty()) {
        return;
      }
      PDDocument.load(convertByteArrayToInputStream(pdf.get().getPdfBytes()))
          .save(response.getOutputStream());
    } catch (IOException ioException) {
      System.out.println(ioException.getMessage());
    }
  }

  public List<PdfEntity> getAllPdfsByEmail(final String userEmail) {
    List<PdfEntity> pdfEntityList =
        Objects.isNull(userEmail)
            ? pdfRepository.findAll()
            : pdfRepository.findAllByUserEmail(userEmail);

    if (pdfEntityList.isEmpty()) {
      return Collections.emptyList();
    }

    return pdfEntityList;
  }

  public static ByteArrayInputStream convertByteArrayToInputStream(byte[] byteArray) {
    return new ByteArrayInputStream(byteArray);
  }
}
