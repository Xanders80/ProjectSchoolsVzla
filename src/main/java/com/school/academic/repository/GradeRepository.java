package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.entity.Course;
import com.school.academic.entity.Grade;
import com.school.academic.entity.Student;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentAndCourseAndPeriod(Student student, Course course, AcademicPeriod period);

    List<Grade> findByStudentIdAndDeletedFalseOrderByDateDesc(Long studentId);

    List<Grade> findByCourseId(Long courseId);

    @org.springframework.data.jpa.repository.Query("SELECT g.course.name, AVG(g.score) FROM Grade g GROUP BY g.course.name")
    List<Object[]> findAverageGradeByCourse();

    @Modifying
    void deleteByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(g) FROM Grade g WHERE g.course.id = ?1")
    long countByCourseId(Long courseId);

    List<Grade> findByDeletedFalse();

    @org.springframework.data.jpa.repository.Query("SELECT AVG(g.score) FROM Grade g")
    Double findAverageGrade();
}
