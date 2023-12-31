package com.ulbra.metroplanpdfautomation.domain.entities;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "pdfs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Lob private byte[] pdfBytes;

  private String userEmail;

  @Lob
  private String base64Image;

  private String userName;
}
