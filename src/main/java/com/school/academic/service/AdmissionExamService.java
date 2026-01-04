package com.school.academic.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.dto.AdmissionResult;
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

    public Page<AdmissionExam> getAllExams(@NonNull Pageable pageable) {
        return admissionExamRepository.findAll(pageable);
    }

    public Optional<AdmissionExam> getExamById(@NonNull Long id) {
        return admissionExamRepository.findById(id);
    }

    public AdmissionExam saveExam(@NonNull AdmissionExam exam) {
        return admissionExamRepository.save(exam);
    }

    public void deleteExam(@NonNull Long id) {
        admissionExamRepository.deleteById(id);
    }

    public Optional<AdmissionExam> findByApplicantDni(@NonNull String dni) {
        return admissionExamRepository.findByApplicantDni(dni);
    }

    /**
     * Approves an admission exam
     */
    public AdmissionExam approveExam(@NonNull Long examId) {
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
    public AdmissionExam rejectExam(@NonNull Long examId, String reason) {
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
     * Processes exam results and determines admission status
     */
    public AdmissionResult processExam(@NonNull Long examId, Double score) {
        AdmissionExam exam = admissionExamRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examen de admisión no encontrado"));

        exam.setScore(score);

        // Determine approval based on score
        boolean approved = score >= 70.0; // Minimum passing score
        String status = approved ? "APPROVED" : "REJECTED";
        String recommendation = generateRecommendation(score);

        exam.setStatus(status);
        exam.setComments(recommendation);
        admissionExamRepository.save(exam);

        return new AdmissionResult(
                examId, exam.getApplicantName(), status, score, approved);
    }

    private String generateRecommendation(Double score) {
        if (score >= 90)
            return "Excelente candidato - Admisión inmediata";
        if (score >= 80)
            return "Buen candidato - Admisión recomendada";
        if (score >= 70)
            return "Candidato aceptable - Admisión condicional";
        return "Puntaje insuficiente - No admitido";
    }

    public com.school.academic.entity.Student enrollApplicant(@NonNull Long examId) {
        AdmissionExam exam = admissionExamRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examen de admisión no encontrado"));

        if (!"APPROVED".equals(exam.getStatus())) {
            throw new IllegalStateException("Solo se pueden inscribir postulantes aprobados");
        }

        // Validar email único
        if (userService.findByEmail(exam.getApplicantEmail()).isPresent()) {
            throw new IllegalStateException("El email ya está registrado en el sistema");
        }

        // Validar DNI único
        if (userService.findByUsername(exam.getApplicantDni()).isPresent()) {
            throw new IllegalStateException("El DNI ya está registrado como usuario");
        }

        // Check if already enrolled
        @SuppressWarnings("null")
        Optional<com.school.academic.entity.Student> existing = studentService.getStudentByDni(exam.getApplicantDni());
        if (existing.isPresent()) {
            throw new IllegalStateException("El postulante ya está inscrito como estudiante");
        }

        // Validar formato de nombre
        if (exam.getApplicantName() == null || exam.getApplicantName().trim().length() < 3) {
            throw new IllegalStateException("El nombre del postulante es inválido");
        }

        // Create Student entity
        com.school.academic.entity.Student student = new com.school.academic.entity.Student();

        // Parse name (simple split - in production you'd want better name parsing)
        String[] nameParts = exam.getApplicantName().trim().split("\\s+", 2);
        student.setFirstName(nameParts[0]);
        student.setLastName(nameParts.length > 1 ? nameParts[1] : "Sin Apellido");

        student.setDni(exam.getApplicantDni());
        student.setEmail(exam.getApplicantEmail());
        student.setEnrollmentDate(java.time.LocalDate.now());

        // Save student (registration number generated automatically)
        student = studentService.saveStudent(student);

        // Create User account using registerNewUser with secure password
        String securePassword = com.school.core.util.PasswordGenerator.generateSecurePassword();
        com.school.core.entity.User user = userService.registerNewUser(
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                exam.getApplicantDni(), // Use DNI as username
                securePassword);

        // Link user to student
        student.setUser(user);
        student = studentService.saveStudent(student);

        // Update exam status
        exam.setStatus("ENROLLED");
        exam.setComments("Inscrito exitosamente. Contraseña temporal generada.");
        admissionExamRepository.save(exam);

        return student;
    }
}
