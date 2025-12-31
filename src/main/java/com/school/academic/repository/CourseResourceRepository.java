package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.CourseResource;

@Repository
public interface CourseResourceRepository extends JpaRepository<CourseResource, Long> {
    List<CourseResource> findByCourseId(Long courseId);
}
