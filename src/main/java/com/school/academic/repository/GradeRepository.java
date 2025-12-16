package com.school.academic.repository;

import com.school.academic.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentIdOrderByDateDesc(Long studentId);

    List<Grade> findByCourseId(Long courseId);

    @org.springframework.data.jpa.repository.Query("SELECT g.course.name, AVG(g.score) FROM Grade g GROUP BY g.course.name")
    List<Object[]> findAverageGradeByCourse();
}
