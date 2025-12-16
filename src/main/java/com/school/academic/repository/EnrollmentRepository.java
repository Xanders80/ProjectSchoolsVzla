package com.school.academic.repository;

import com.school.academic.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findBySectionId(Long sectionId);

    @org.springframework.data.jpa.repository.Query("SELECT e.section.name, COUNT(e) FROM Enrollment e GROUP BY e.section.name")
    List<Object[]> countStudentsBySection();
}
