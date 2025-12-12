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

    public AcademicService(StudentRepository studentRepository,
            com.school.academic.repository.CourseRepository courseRepository,
            com.school.academic.repository.SectionRepository sectionRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
    }

    // Student Ops...

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
}
