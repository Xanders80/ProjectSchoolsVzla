package com.school.academic.repository;

import com.school.academic.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourseId(Long courseId);
    
    @Query("SELECT s FROM Section s WHERE s.deleted = false")
    Page<Section> findAllActive(Pageable pageable);
    
    @Query("SELECT s FROM Section s WHERE s.id = ?1 AND s.deleted = false")
    Optional<Section> findByIdAndNotDeleted(Long id);
    
    @Query("SELECT COUNT(s) > 0 FROM Section s WHERE s.id = ?1 AND s.deleted = false")
    boolean existsByIdAndNotDeleted(Long id);
}
