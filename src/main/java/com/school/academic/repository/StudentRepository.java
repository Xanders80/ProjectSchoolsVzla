package com.school.academic.repository;

import com.school.academic.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String regNum);

    Optional<Student> findByDni(String dni);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s WHERE s.deleted = false")
    java.util.List<Student> findAllActive();

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s WHERE s.id = ?1 AND s.deleted = false")
    Optional<Student> findByIdAndNotDeleted(Long id);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByUserId(Long userId);

    org.springframework.data.domain.Page<Student> findByDeletedFalse(org.springframework.data.domain.Pageable pageable);

    Optional<Student> findTopByRegistrationNumberStartingWithOrderByRegistrationNumberDesc(String prefix);

    long countByDeletedFalse();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT s.id) FROM Student s WHERE s.id IN (SELECT a.student.id FROM Attendance a WHERE a.status = 'ABSENT' AND a.date BETWEEN ?1 AND ?2 GROUP BY a.student.id HAVING COUNT(a.id) >= 3)")
    long countStudentsWithExcessiveAbsences(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
