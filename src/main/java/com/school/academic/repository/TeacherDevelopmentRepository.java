package com.school.academic.repository;

import java.util.List;
import com.school.academic.entity.TeacherDevelopment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherDevelopmentRepository extends JpaRepository<TeacherDevelopment, Long> {
    List<TeacherDevelopment> findByTeacherProfileId(Long teacherProfileId);

    List<TeacherDevelopment> findByTeacherProfileIdAndVerifiedTrue(Long teacherProfileId);
}
