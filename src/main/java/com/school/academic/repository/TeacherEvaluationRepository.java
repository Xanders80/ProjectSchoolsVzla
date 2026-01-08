package com.school.academic.repository;

import java.util.Optional;
import com.school.academic.entity.TeacherEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherEvaluationRepository extends JpaRepository<TeacherEvaluation, Long> {
    Optional<TeacherEvaluation> findByTeacherProfileIdAndAcademicPeriodId(Long teacherProfileId, Long academicPeriodId);

    java.util.List<TeacherEvaluation> findByTeacherProfileId(Long teacherProfileId);
}
