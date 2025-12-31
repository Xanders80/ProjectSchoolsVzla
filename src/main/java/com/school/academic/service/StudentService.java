package com.school.academic.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Student;
import com.school.academic.repository.StudentRepository;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> getAllStudents(@NonNull Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Optional<Student> getStudentById(@NonNull Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByDni(@NonNull String dni) {
        return studentRepository.findByDni(dni);
    }

    public Student saveStudent(Student student) {
        // Generate registration number if not set
        if (student.getRegistrationNumber() == null || student.getRegistrationNumber().isEmpty()) {
            student.setRegistrationNumber(generateRegistrationNumber());
        }

        // Set enrollment date if not set
        if (student.getEnrollmentDate() == null) {
            student.setEnrollmentDate(LocalDate.now());
        }

        return studentRepository.save(student);
    }

    public void deleteStudent(@NonNull Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
        student.setDeleted(true);
        studentRepository.save(student);
    }

    /**
     * Generates a unique registration number in format: STU-YYYY-NNNN
     * Example: STU-2024-0001
     */
    private String generateRegistrationNumber() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "STU-" + year + "-";

        // Find the last registration number for this year
        String lastNumber = studentRepository
                .findTopByRegistrationNumberStartingWithOrderByRegistrationNumberDesc(prefix)
                .map(Student::getRegistrationNumber)
                .orElse(prefix + "0000");

        // Extract and increment the sequence number
        String sequencePart = lastNumber.substring(prefix.length());
        int nextSequence = Integer.parseInt(sequencePart) + 1;

        return prefix + String.format("%04d", nextSequence);
    }

    public long countActiveStudents() {
        return studentRepository.countByDeletedFalse();
    }
}
