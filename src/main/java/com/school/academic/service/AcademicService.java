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
@Transactional(readOnly = true)
public class AcademicService {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final CourseService courseService;
    private final SectionService sectionService;
    private final com.school.academic.repository.GradeRepository gradeRepository;
    private final com.school.academic.repository.AttendanceRepository attendanceRepository;
    private final com.school.academic.repository.EnrollmentRepository enrollmentRepository;
    private final com.school.health.service.HealthService healthService;
    private final com.school.core.service.AuditService auditService;

    public AcademicService(StudentRepository studentRepository,
            StudentService studentService,
            CourseService courseService,
            SectionService sectionService,
            com.school.academic.repository.GradeRepository gradeRepository,
            com.school.academic.repository.AttendanceRepository attendanceRepository,
            com.school.academic.repository.EnrollmentRepository enrollmentRepository,
            com.school.health.service.HealthService healthService,
            com.school.core.service.AuditService auditService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
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

	@Transactional
	public void saveAttendanceList(@NonNull java.util.List<com.school.academic.entity.Attendance> attendanceList) {
        attendanceRepository.saveAll(attendanceList);
    }

    public java.util.List<com.school.academic.entity.Attendance> getAttendanceByStudent(@NonNull Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    // Grade Ops
	@Transactional
	public com.school.academic.entity.Grade saveGrade(@NonNull com.school.academic.entity.Grade grade) {
        return gradeRepository.save(grade);
    }

    public java.util.List<com.school.academic.entity.Grade> getGradesByStudent(@NonNull Long studentId) {
        return gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(studentId);
    }

	@Transactional
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
	@Transactional
	public com.school.academic.entity.Section saveSection(@NonNull com.school.academic.entity.Section section) {
        return sectionService.saveSection(section);
    }

	@Transactional
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

	@Transactional
	public Student saveStudent(@NonNull Student student) {
        // Delegate to StudentService which handles logic like auto-generation of
        // registration number
        return studentService.saveStudent(student);
    }

	@Transactional
	public void deleteStudent(@NonNull Long id) {
        // Verificar enrollments existentes
        if (enrollmentRepository.existsByStudentId(id)) {
            throw new IllegalStateException("No se puede eliminar el estudiante con inscripciones existentes");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        // Soft delete
        student.setDeleted(true);
        studentRepository.save(student);

        // Auditoría
        auditService.logStudentDeletion(id, getCurrentUser());
    }

	@Transactional
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

	@Transactional
	public void enrollStudent(@NonNull Long studentId, @NonNull Long sectionId) {
        Section section = sectionService.getSectionById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        // Validar que el estudiante no esté ya en ninguna sección del MISMO periodo
        // académico
        Long periodId = section.getPeriod().getId();
        java.util.List<Long> enrolledInPeriod = enrollmentRepository.findActiveStudentIdsByPeriodId(periodId);
        if (enrolledInPeriod.contains(studentId)) {
            throw new IllegalStateException("El estudiante ya tiene una inscripción activa en este periodo académico.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setEnrollmentDate(java.time.LocalDateTime.now());

        enrollmentRepository.save(enrollment);
        auditService.logGenericAction("ENROLL_STUDENT",
                "El estudiante " + studentId + " se inscribió en la sección " + sectionId,
                getCurrentUser());
    }

	@Transactional
	public void unenrollStudent(@NonNull Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripcion no encontrada"));

        // Verificación de nulabilidad para enrollment.getStudent()
        if (enrollment.getStudent() == null) {
            throw new IllegalStateException("Inscripcion no tiene estudiante asociado: " + enrollmentId);
        }

        if (enrollment.getStudent().getId() == null) {
            throw new IllegalStateException("Estudiante asociado a la inscripcion no tiene ID válido: " + enrollmentId);
        }

        enrollmentRepository.delete(enrollment);
        auditService.logGenericAction("UNENROLL_STUDENT",
                "Se desinscribió el estudiante " + enrollment.getStudent().getId() + " de la sección "
                        + enrollment.getSection().getId(),
                getCurrentUser());
    }

    /**
     * Transfiere un estudiante de una sección a otra en una operación atómica.
     */
	@Transactional
	public void transferStudent(@NonNull Long studentId, @NonNull Long fromSectionId, @NonNull Long toSectionId) {
        if (fromSectionId.equals(toSectionId)) {
            throw new IllegalArgumentException("La sección de origen y destino deben ser diferentes.");
        }

        Enrollment currentEnrollment = enrollmentRepository.findBySectionId(fromSectionId).stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("El estudiante no está inscrito en la sección de origen."));

        Section toSection = sectionService.getSectionById(toSectionId)
                .orElseThrow(() -> new IllegalArgumentException("La sección de destino no existe."));

        // Verificar si ya está en la de destino
        if (enrollmentRepository.findBySectionId(toSectionId).stream()
                .anyMatch(e -> e.getStudent().getId().equals(studentId))) {
            throw new IllegalStateException("El estudiante ya está inscrito en la sección de destino.");
        }

        // Realizar la transferencia
        currentEnrollment.setSection(toSection);
        currentEnrollment.setEnrollmentDate(java.time.LocalDateTime.now());
        enrollmentRepository.save(currentEnrollment);

        auditService.logGenericAction("TRANSFER_STUDENT",
                "Student " + studentId + " transferred from section " + fromSectionId + " to " + toSectionId,
                getCurrentUser());
    }

    /**
     * Reinscribe estudiantes de forma masiva para un nuevo periodo académico.
     */
	@Transactional
	public void batchReenroll(@NonNull Long nextSectionId, @NonNull java.util.List<Long> studentIds) {
        Section section = sectionService.getSectionById(nextSectionId)
                .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));

        for (Long studentId : studentIds) {
            java.util.Objects.requireNonNull(studentId, "El ID del estudiante en la lista no puede ser nulo");

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + studentId));

            // Evitar duplicados en el mismo periodo/sección
            boolean alreadyEnrolled = enrollmentRepository.findBySectionId(nextSectionId).stream()
                    .anyMatch(e -> e.getStudent().getId().equals(studentId));

            if (!alreadyEnrolled) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setSection(section);
                enrollment.setStatus(com.school.academic.enums.EnrollmentStatus.ACTIVE);
                enrollment.setEnrollmentDate(java.time.LocalDateTime.now());
                enrollmentRepository.save(enrollment);
            }
        }

        auditService.logGenericAction("BATCH_REENROLL",
                "Reinscripción masiva completada para la sección " + nextSectionId + " estudiantes: "
                        + studentIds.size(),
                getCurrentUser());
    }

    public java.util.List<Student> getStudentsNotInSection(@NonNull Long sectionId) {
        Section section = sectionService.getSectionById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));

        return enrollmentRepository.findStudentsNotEnrolledInPeriod(section.getPeriod().getId());
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
