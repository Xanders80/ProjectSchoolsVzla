package com.school.academic.service;

import com.school.academic.entity.Student;
import com.school.academic.repository.StudentRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        return gradeRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    public void deleteGrade(@NonNull Long id) {
        gradeRepository.deleteById(id);
    }

    public Optional<com.school.academic.entity.Grade> getGradeById(@NonNull Long id) {
        return gradeRepository.findById(id);
    }

    public java.util.List<com.school.academic.entity.Grade> getAllGrades() {
        return gradeRepository.findAll();
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
        return studentRepository.findAll(pageable);
    }

    public Optional<Student> getStudentById(@NonNull Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(@NonNull Student student) {
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

    private String getCurrentUser() {
        return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public java.util.List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }
}
