package com.school.academic.service;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Section;
import com.school.academic.entity.Enrollment;
import com.school.academic.entity.Student;
import com.school.academic.repository.StudentRepository;

@Service
@Transactional
public class AcademicService {

    private final StudentRepository studentRepository;
    private final com.school.academic.service.CourseService courseService;
    private final com.school.academic.service.SectionService sectionService;
    private final com.school.academic.repository.GradeRepository gradeRepository;
    private final com.school.academic.repository.AttendanceRepository attendanceRepository;
    private final com.school.academic.repository.EnrollmentRepository enrollmentRepository;
    private final com.school.health.service.HealthService healthService;
    private final com.school.core.service.AuditService auditService;

    public AcademicService(StudentRepository studentRepository,
            com.school.academic.service.CourseService courseService,
            com.school.academic.service.SectionService sectionService,
            com.school.academic.repository.GradeRepository gradeRepository,
            com.school.academic.repository.AttendanceRepository attendanceRepository,
            com.school.academic.repository.EnrollmentRepository enrollmentRepository,
            com.school.health.service.HealthService healthService,
            com.school.core.service.AuditService auditService) {
        this.studentRepository = studentRepository;
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.healthService = healthService;
        this.auditService = auditService;
    }

    // Student Ops...

    // Attendance Ops
    public java.util.List<com.school.academic.entity.Attendance> getAttendanceBySectionAndDate(@NonNull Long sectionId,
            @NonNull java.time.LocalDate date) {
        return attendanceRepository.findBySectionIdAndDate(sectionId, date);
    }

    public void saveAttendanceList(@NonNull java.util.List<com.school.academic.entity.Attendance> attendanceList) {
        attendanceRepository.saveAll(attendanceList);
    }

    public java.util.List<com.school.academic.entity.Attendance> getAttendanceByStudent(@NonNull Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    // Grade Ops
    public com.school.academic.entity.Grade saveGrade(@NonNull com.school.academic.entity.Grade grade) {
        return gradeRepository.save(grade);
    }

    public java.util.List<com.school.academic.entity.Grade> getGradesByStudent(@NonNull Long studentId) {
        return gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(studentId);
    }

    public void deleteGrade(@NonNull Long id) {
        com.school.academic.entity.Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));
        grade.setDeleted(true);
        grade.setDeletedAt(java.time.LocalDateTime.now());
        grade.setDeletedBy(getCurrentUser());
        gradeRepository.save(grade);
    }

    public Optional<com.school.academic.entity.Grade> getGradeById(@NonNull Long id) {
        return gradeRepository.findById(id);
    }

    public java.util.List<com.school.academic.entity.Grade> getAllGrades() {
        return gradeRepository.findByDeletedFalse();
    }

    // Course Ops - Delegated to CourseService
    public java.util.List<com.school.academic.entity.Course> getAllCourses() {
        return courseService.getAllActiveCourses();
    }

    // Section Ops - Delegated to SectionService
    public com.school.academic.entity.Section saveSection(@NonNull com.school.academic.entity.Section section) {
        return sectionService.saveSection(section);
    }

    public void deleteSection(@NonNull Long id) {
        sectionService.deleteSection(id);
    }

    public Optional<com.school.academic.entity.Section> getSectionById(@NonNull Long id) {
        return sectionService.getSectionById(id);
    }

    public java.util.List<com.school.academic.entity.Section> getAllSections() {
        return sectionService.getAllActiveSections(org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public org.springframework.data.domain.Page<Student> getAllStudents(
            @NonNull org.springframework.data.domain.Pageable pageable) {
        return studentRepository.findByDeletedFalse(pageable);
    }

    public Optional<Student> getStudentById(@NonNull Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(@NonNull Student student) {
        // Verificar DNI único
        if (student.getDni() != null) {
            studentRepository.findByDni(student.getDni())
                    .ifPresent(existing -> {
                        if (student.getId() == null || !existing.getId().equals(student.getId())) {
                            throw new IllegalArgumentException("El DNI ya está registrado para otro estudiante.");
                        }
                    });
        }

        // Verificar Número de Registro único
        if (student.getRegistrationNumber() != null) {
            studentRepository.findByRegistrationNumber(student.getRegistrationNumber())
                    .ifPresent(existing -> {
                        if (student.getId() == null || !existing.getId().equals(student.getId())) {
                            throw new IllegalArgumentException(
                                    "El número de registro ya está asignado a otro estudiante.");
                        }
                    });
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(@NonNull Long id) {
        // Verificar enrollments existentes
        if (enrollmentRepository.existsByStudentId(id)) {
            throw new IllegalStateException("Cannot delete student with existing enrollments");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // Soft delete
        student.setDeleted(true);
        studentRepository.save(student);

        // Auditoría
        auditService.logStudentDeletion(id, getCurrentUser());
    }

    public void hardDeleteStudent(@NonNull Long id) {
        // 1. Delete health data
        healthService.deleteStudentHealthData(id);

        // 2. Delete academic data
        gradeRepository.deleteByStudentId(id);
        attendanceRepository.deleteByStudentId(id);
        enrollmentRepository.deleteByStudentId(id);

        // 3. Delete student
        studentRepository.deleteById(id);

        // Auditoría
        auditService.logStudentDeletion(id, getCurrentUser());
    }

    // Enrollment Ops
    public java.util.List<Enrollment> getEnrollmentsBySection(@NonNull Long sectionId) {
        return enrollmentRepository.findBySectionIdAndStudentDeletedFalse(sectionId);
    }

    public void enrollStudent(@NonNull Long studentId, @NonNull Long sectionId) {
        Section section = sectionService.getSectionById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (enrollmentRepository.findBySectionId(sectionId).stream()
                .anyMatch(e -> e.getStudent().getId().equals(studentId))) {
            throw new IllegalStateException("El estudiante ya está inscrito en esta sección.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setEnrollmentDate(java.time.LocalDateTime.now());

        enrollmentRepository.save(enrollment);
        auditService.logGenericAction("ENROLL_STUDENT", "Student " + studentId + " enrolled in section " + sectionId,
                getCurrentUser());
    }

    public void unenrollStudent(@NonNull Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
        auditService.logGenericAction("UNENROLL_STUDENT",
                "Enrollment " + enrollmentId + " removed (Student: " + enrollment.getStudent().getId() + ")",
                getCurrentUser());
    }

    public java.util.List<Student> getStudentsNotInSection(@NonNull Long sectionId) {
        java.util.List<Long> enrolledStudentIds = enrollmentRepository.findBySectionId(sectionId).stream()
                .map(e -> e.getStudent().getId())
                .collect(java.util.stream.Collectors.toList());

        if (enrolledStudentIds.isEmpty()) {
            return studentRepository.findAllActive();
        }

        return studentRepository.findAllActive().stream()
                .filter(s -> !enrolledStudentIds.contains(s.getId()))
                .collect(java.util.stream.Collectors.toList());
    }

    @NonNull
    private String getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName();
            if (name != null) {
                return name;
            }
        }
        return "system";
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public java.util.List<Student> getAllStudents() {
        return studentRepository.findAllActive();
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }
}
