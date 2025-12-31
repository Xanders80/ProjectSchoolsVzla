package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.academic.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findBySectionId(Long sectionId);

    List<Enrollment> findBySectionIdAndStudentDeletedFalse(Long sectionId);

    @org.springframework.data.jpa.repository.Query("SELECT e.section.name, COUNT(e) FROM Enrollment e GROUP BY e.section.name")
    List<Object[]> countStudentsBySection();

    boolean existsByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    void deleteByStudentId(Long studentId);

    boolean existsBySectionId(Long sectionId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(e) FROM Enrollment e WHERE e.section.id = ?1")
    long countBySectionId(Long sectionId);
}
