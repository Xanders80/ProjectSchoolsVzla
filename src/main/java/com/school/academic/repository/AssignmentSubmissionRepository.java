package com.school.academic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.Assignment;
import com.school.academic.entity.AssignmentSubmission;
import com.school.academic.entity.Student;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment(Assignment assignment);

    Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, Student student);

    List<AssignmentSubmission> findByStudent(Student student);
}
