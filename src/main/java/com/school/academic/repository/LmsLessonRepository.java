package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.LmsLesson;
import com.school.academic.entity.LmsModule;

@Repository
public interface LmsLessonRepository extends JpaRepository<LmsLesson, Long> {
    List<LmsLesson> findByModuleOrderBySortOrderAsc(LmsModule module);

    List<LmsLesson> findByModuleAndPublishedTrueOrderBySortOrderAsc(LmsModule module);
}
