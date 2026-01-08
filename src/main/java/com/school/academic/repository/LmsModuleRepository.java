package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.Course;
import com.school.academic.entity.LmsModule;

@Repository
public interface LmsModuleRepository extends JpaRepository<LmsModule, Long> {
    List<LmsModule> findByCourseOrderBySortOrderAsc(Course course);

    List<LmsModule> findByCourseAndPublishedTrueOrderBySortOrderAsc(Course course);
}
