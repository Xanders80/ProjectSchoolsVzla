package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.AdmissionExam;
import com.school.academic.repository.AdmissionExamRepository;

@Service
@Transactional
public class AdmissionExamService {

    private final AdmissionExamRepository admissionExamRepository;
    private final StudentService studentService;
    private final com.school.core.service.UserService userService;

    public AdmissionExamService(AdmissionExamRepository admissionExamRepository,
            StudentService studentService,
            com.school.core.service.UserService userService) {
        this.admissionExamRepository = admissionExamRepository;
        this.studentService = studentService;
        this.userService = userService;
    }

    public Page<AdmissionExam> getAllExams(Pageable pageable) {
        return admissionExamRepository.findAll(pageable);
    }

    public Optional<AdmissionExam> getExamById(Long id) {
        return admissionExamRepository.findById(id);
    }

    public AdmissionExam saveExam(AdmissionExam exam) {
        return admissionExamRepository.save(exam);
    }

    public void deleteExam(Long id) {
        admissionExamRepository.deleteById(id);
    }

    public Optional<AdmissionExam> findByApplicantDni(String dni) {
        return admissionExamRepository.findByApplicantDni(dni);
    }

    /**
     * Approves an admission exam
     */
    public AdmissionExam approveExam(Long examId) {
        AdmissionExam exam = admissionExamRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examen de admisión no encontrado"));

        if (!"PENDING".equals(exam.getStatus())) {
            throw new IllegalStateException("Solo se pueden aprobar exámenes pendientes");
        }

        exam.setStatus("APPROVED");
        return admissionExamRepository.save(exam);
    }

    /**
     * Rejects an admission exam
     */
    public AdmissionExam rejectExam(Long examId, String reason) {
        AdmissionExam exam = admissionExamRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examen de admisión no encontrado"));

        if (!"PENDING".equals(exam.getStatus())) {
            throw new IllegalStateException("Solo se pueden rechazar exámenes pendientes");
        }

        exam.setStatus("REJECTED");
        exam.setComments(reason != null ? reason : "Rechazado");
        return admissionExamRepository.save(exam);
    }

    /**
     * Enrolls an approved applicant as a student
     * Creates a Student entity and associated User account
     */
    public com.school.academic.entity.Student enrollApplicant(Long examId) {
        AdmissionExam exam = admissionExamRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examen de admisión no encontrado"));

        if (!"APPROVED".equals(exam.getStatus())) {
            throw new IllegalStateException("Solo se pueden inscribir postulantes aprobados");
        }

        // Check if already enrolled
        Optional<com.school.academic.entity.Student> existing = studentService.getStudentByDni(exam.getApplicantDni());
        if (existing.isPresent()) {
            throw new IllegalStateException("El postulante ya está inscrito como estudiante");
        }

        // Create Student entity
        com.school.academic.entity.Student student = new com.school.academic.entity.Student();

        // Parse name (simple split - in production you'd want better name parsing)
        String[] nameParts = exam.getApplicantName().trim().split("\\s+", 2);
        student.setFirstName(nameParts[0]);
        student.setLastName(nameParts.length > 1 ? nameParts[1] : "");

        student.setDni(exam.getApplicantDni());
        student.setEmail(exam.getApplicantEmail());
        student.setEnrollmentDate(java.time.LocalDate.now());

        // Save student (registration number generated automatically)
        student = studentService.saveStudent(student);

        // Create User account using registerNewUser
        com.school.core.entity.User user = userService.registerNewUser(
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                exam.getApplicantDni(), // Use DNI as username
                exam.getApplicantDni() + "2024!" // Simple password that meets requirements
        );

        // Link user to student
        student.setUser(user);
        student = studentService.saveStudent(student);

        // Update exam status
        exam.setStatus("ENROLLED");
        admissionExamRepository.save(exam);

        return student;
    }
}
