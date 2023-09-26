package com.ulbra.metroplanpdfautomation.controllers;

import com.ulbra.metroplanpdfautomation.DTOs.StudentInformation;
import com.ulbra.metroplanpdfautomation.services.PdfEditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.ulbra.metroplanpdfautomation.controllers.ServletHeaders.CONTENT_DISPOSITION_HEADER;
import static com.ulbra.metroplanpdfautomation.controllers.ServletHeaders.FILE_ATTACHMENT_HEADER;

@RestController
@RequestMapping(value = "/pdf")
public class PdfEditorController {

    private final PdfEditorService pdfEditorService;
    @Autowired
    public PdfEditorController(final PdfEditorService pdfEditorService){
        this.pdfEditorService = pdfEditorService;
    }

    @GetMapping("/generateDeclaration")
    public void generatePDF(
            final HttpServletResponse response,
            @RequestBody(required = false) final MultipartFile document,
            @RequestParam(required = false) final String studentName,
            @RequestParam(required = false) final String studentEmail,
            @RequestParam(required = false) final List<String> studentPresencialDays){


        StudentInformation studentInformation=StudentInformation
                .builder()
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
}
