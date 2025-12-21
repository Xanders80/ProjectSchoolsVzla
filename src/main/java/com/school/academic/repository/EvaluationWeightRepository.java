package com.school.academic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.Course;
import com.school.academic.entity.EvaluationWeight;
import com.school.academic.enums.EvaluationType;

@Repository
public interface EvaluationWeightRepository extends JpaRepository<EvaluationWeight, Long> {
    List<EvaluationWeight> findByCourse(Course course);

    Optional<EvaluationWeight> findByCourseAndEvaluationType(Course course, EvaluationType evaluationType);
}
