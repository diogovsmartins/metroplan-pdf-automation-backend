package com.ulbra.metroplanpdfautomation.controllers;

import static com.ulbra.metroplanpdfautomation.domain.businessEnums.ServletHeaders.CONTENT_DISPOSITION_HEADER;
import static com.ulbra.metroplanpdfautomation.domain.businessEnums.ServletHeaders.FILE_ATTACHMENT_HEADER;

import com.ulbra.metroplanpdfautomation.domain.DTOs.StudentInformation;
import com.ulbra.metroplanpdfautomation.domain.entities.PdfEntity;
import com.ulbra.metroplanpdfautomation.services.PdfEditorService;
import com.ulbra.metroplanpdfautomation.services.PdfEntityService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/pdf")
public class PdfEditorController {

  private final PdfEditorService pdfEditorService;
  private final PdfEntityService pdfEntityService;

  @Autowired
  public PdfEditorController(
      final PdfEditorService pdfEditorService, final PdfEntityService pdfEntityService) {
    this.pdfEditorService = pdfEditorService;
    this.pdfEntityService = pdfEntityService;
  }

  @GetMapping("/generateDeclaration")
  public void generatePDF(
      final HttpServletResponse response,
      @RequestBody(required = false) final MultipartFile document,
      @RequestParam(required = false) final String studentName,
      @RequestParam(required = false) final String studentEmail,
      @RequestParam(required = false) final List<String> studentPresencialDays) {

    StudentInformation studentInformation =
        StudentInformation.builder()
            .name(studentName)
            .email(studentEmail)
            .presencialDays(studentPresencialDays)
            .build();

    response.setContentType("application/pdf");
    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
    String currentDateTime = dateFormatter.format(new Date());

    String headerKey = CONTENT_DISPOSITION_HEADER.value;
    String headerValue = FILE_ATTACHMENT_HEADER.value + currentDateTime + ".pdf";
    response.setHeader(headerKey, headerValue);

    this.pdfEditorService.generatePresencialityDeclaration(response, studentInformation, document);
  }

  @GetMapping("/id")
  public void getPdfById(
      final HttpServletResponse response, @RequestParam(required = false) final Long id) {

    pdfEntityService.getPdfById(id, response);

    response.setContentType("application/pdf");
    String headerKey = CONTENT_DISPOSITION_HEADER.value;
    String headerValue = FILE_ATTACHMENT_HEADER.value + ".pdf";
    response.setHeader(headerKey, headerValue);
  }

  @GetMapping()
  public ResponseEntity<List<PdfEntity>> getAllPdfsByEmail(
      @RequestParam(required = false) final String userEmail) {

    return ResponseEntity.ok().body(pdfEntityService.getAllPdfsByEmail(userEmail));
  }
}
