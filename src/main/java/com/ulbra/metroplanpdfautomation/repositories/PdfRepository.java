package com.ulbra.metroplanpdfautomation.repositories;

import com.ulbra.metroplanpdfautomation.entities.PdfEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfRepository extends JpaRepository<PdfEntity, Long> {

  List<PdfEntity> findAllByUserEmail(final String userEmail);
}
