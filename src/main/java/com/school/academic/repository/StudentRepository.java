package com.school.academic.repository;

import com.school.academic.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String regNum);
    Optional<Student> findByDni(String dni);
}
