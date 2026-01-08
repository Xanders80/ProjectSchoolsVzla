package com.school.academic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.Assignment;
import com.school.academic.entity.LmsModule;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByModuleOrderByDueDateAsc(LmsModule module);

    List<Assignment> findByModuleAndPublishedTrueOrderByDueDateAsc(LmsModule module);
}
