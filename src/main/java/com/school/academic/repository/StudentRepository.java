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

}
