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
    private final com.school.academic.repository.CourseRepository courseRepository;
    private final com.school.academic.repository.SectionRepository sectionRepository;
    private final com.school.academic.repository.GradeRepository gradeRepository;
    private final com.school.academic.repository.AttendanceRepository attendanceRepository;

    public AcademicService(StudentRepository studentRepository,
            com.school.academic.repository.CourseRepository courseRepository,
            com.school.academic.repository.SectionRepository sectionRepository,
            com.school.academic.repository.GradeRepository gradeRepository,
            com.school.academic.repository.AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
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

    // Course Ops
    public java.util.List<com.school.academic.entity.Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Section Ops
    public java.util.List<com.school.academic.entity.Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public com.school.academic.entity.Section saveSection(@NonNull com.school.academic.entity.Section section) {
        return sectionRepository.save(section);
    }

    public void deleteSection(@NonNull Long id) {
        sectionRepository.deleteById(id);
    }

    public Optional<com.school.academic.entity.Section> getSectionById(@NonNull Long id) {
        return sectionRepository.findById(id);
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
        studentRepository.deleteById(id);
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public java.util.List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
