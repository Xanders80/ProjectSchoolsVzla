package com.school.academic.repository;

import com.school.academic.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentIdOrderByDateDesc(Long studentId);

    List<Grade> findByCourseId(Long courseId);

    @org.springframework.data.jpa.repository.Query("SELECT g.course.name, AVG(g.score) FROM Grade g GROUP BY g.course.name")
    List<Object[]> findAverageGradeByCourse();

    @Modifying
    void deleteByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(g) FROM Grade g WHERE g.course.id = ?1")
    long countByCourseId(Long courseId);
}
