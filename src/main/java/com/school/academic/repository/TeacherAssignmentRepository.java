package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.school.academic.entity.TeacherAssignment;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {
    
    List<TeacherAssignment> findByTeacherProfileIdAndIsActiveTrue(Long teacherProfileId);
    
    List<TeacherAssignment> findByCourseIdAndIsActiveTrue(Long courseId);
    
    List<TeacherAssignment> findByAcademicPeriodIdAndIsActiveTrue(Long academicPeriodId);
    
    @Query("SELECT SUM(ta.assignedHours) FROM TeacherAssignment ta WHERE ta.teacherProfile.id = ?1 AND ta.isActive = true")
    Integer getTotalHoursByTeacher(Long teacherProfileId);
    
    boolean existsByTeacherProfileIdAndCourseIdAndAcademicPeriodIdAndIsActiveTrue(
        Long teacherProfileId, Long courseId, Long academicPeriodId);
}