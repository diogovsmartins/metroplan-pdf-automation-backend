package com.ulbra.metroplanpdfautomation.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.ulbra.metroplanpdfautomation.DTOs.StudentInformation;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class PdfEditorService {
    public static final String PRESENCIALITY_DECLARATION_TEMPLATE = "Declaro para devidos fins que o aluno %s está\n" +
            "regularmente matriculado de 31 de Julho a 21 de Dezembro de 2023,\n" +
            "correspondente ao segundo semestre letivo do corrente ano, e terá suas\n" +
            "aulas presenciais nos dias %s, conforme o atestado de\n" +
            "matrícula em anexo.";
    Logger LOGGER=LoggerFactory.getLogger(PdfEditorService.class);
    public void generatePresencialityDeclaration(
            final HttpServletResponse response,
            final StudentInformation studentInformation,
            final MultipartFile multipartFile) {
        //Extract MultipartFile to PDFBox document
        PDDocument pdDocument=extractTextFromMultipartFile(multipartFile);
        if (Objects.isNull(pdDocument)){
            return;
        }
        //Create new Itext Document, with pageSize equals to A4
        Document document = new Document(PageSize.A4);
        try{
            //Get the PdfWriter instance passing the IText document in the first param, and the HTTPServletResponse.outputStream()
            //to define where the generate pdf should be sent to, in this case to the HTTPServletResponse so we can return that document.
            PdfWriter.getInstance(document, response.getOutputStream());

            //Open the document and write the needed informations
            document.open();
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(18);

            Paragraph tittleParaghrap = new Paragraph("DECLARAÇÃO", fontTitle);
            tittleParaghrap.setAlignment(Element.ALIGN_CENTER);

            Font firstTextParagraph = FontFactory.getFont(FontFactory.HELVETICA);
            firstTextParagraph.setSize(12);

            Paragraph paragraph2 = new Paragraph(
                    String.format(PRESENCIALITY_DECLARATION_TEMPLATE, studentInformation.getName(), studentInformation.getPresencialDays() ), firstTextParagraph
            );
            paragraph2.setAlignment(Element.ALIGN_CENTER);

            //Add create paragraphs
            document.add(tittleParaghrap);
            document.add(paragraph2);
            //Merge the Itext Document we just created and the PDFBox document we retrieved from the MultipartFile
            mergeDocuments(document, pdDocument).save("mergedDocuments.pdf");
            document.close();
        } catch (IOException e) {
            LOGGER.error("Application threw an IOException when generating the desired PDF.");
        }
    }

    public PDDocument mergeDocuments(final Document iTextDocument, final PDDocument pdDocument){
        try {
            //In theory, we can create this ByteArrayOutputStream to store the iTextDocument
            //Use the PDFWriter, same as we did before, the document and then the outputStream to define where we store
            //the data, after that, what we need is:
            //Open the document, get it's content, write that content to the PDFBox document in a new page, then return
            //the editex PDFBox document and return it
            ByteArrayOutputStream iTextOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(iTextDocument, iTextOutputStream);
            iTextDocument.open();
            pdDocument.addPage(PDDocument.load(iTextOutputStream.toByteArray()).getPage(0));
        }catch (IOException ioException){
            LOGGER.error("Error merging files, error is: {}", ioException.getMessage());
        }
        return pdDocument;
    }


    // This method receives the MultipartFile, uses a standard InputStream to transform that file
    // into a PDFBoxDocument and saves the document to the file inside the save(), makes a log if
    // an IoException is thrown for any particular reason
    public PDDocument extractTextFromMultipartFile(MultipartFile multipartFile){
        try (InputStream inputStream = multipartFile.getInputStream();
             PDDocument pdfDocument = PDDocument.load(inputStream)) {
             pdfDocument.save("enrollmentValidation.pdf");
             return pdfDocument;
        }
        catch (IOException ioException){
            LOGGER.error("Error reading MultipartFile, error: {}", ioException.getMessage());
        }
        return null;
    }
}
