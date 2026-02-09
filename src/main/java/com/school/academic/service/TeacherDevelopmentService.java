package com.school.academic.service;

import com.school.academic.entity.TeacherDevelopment;
import org.springframework.stereotype.Service;
// ...existing code...
import com.school.academic.repository.TeacherDevelopmentRepository;
import java.util.List;

@Service
public class TeacherDevelopmentService {
    private final TeacherDevelopmentRepository teacherDevelopmentRepository;

    public TeacherDevelopmentService(TeacherDevelopmentRepository teacherDevelopmentRepository) {
        this.teacherDevelopmentRepository = teacherDevelopmentRepository;
    }

    public TeacherDevelopment save(@org.springframework.lang.NonNull TeacherDevelopment td) {
        return teacherDevelopmentRepository.save(td);
    }

    public List<TeacherDevelopment> findAll() {
        return teacherDevelopmentRepository.findAll();
    }

    public TeacherDevelopment findById(@org.springframework.lang.NonNull Long id) {
        return teacherDevelopmentRepository.findById(id).orElse(null);
    }

    public void delete(@org.springframework.lang.NonNull Long id) {
        teacherDevelopmentRepository.deleteById(id);
    }
}
