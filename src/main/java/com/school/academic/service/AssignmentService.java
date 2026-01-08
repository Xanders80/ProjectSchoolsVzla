package com.school.academic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Assignment;
import com.school.academic.entity.AssignmentSubmission;
import com.school.academic.entity.LmsModule;
import com.school.academic.entity.Student;
import com.school.academic.repository.AssignmentRepository;
import com.school.academic.repository.AssignmentSubmissionRepository;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository submissionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsByModule(LmsModule module, boolean onlyPublished) {
        if (onlyPublished) {
            return assignmentRepository.findByModuleAndPublishedTrueOrderByDueDateAsc(module);
        }
        return assignmentRepository.findByModuleOrderByDueDateAsc(module);
    }

    @Transactional
    public @NonNull Assignment saveAssignment(@NonNull Assignment assignment) {
        return java.util.Objects.requireNonNull(assignmentRepository.save(assignment));
    }

    @Transactional
    public void deleteAssignment(@NonNull Long id) {
        assignmentRepository.deleteById(id);
    }

    @Transactional
    public AssignmentSubmission submitAssignment(@NonNull AssignmentSubmission submission) {
        return submissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public Optional<AssignmentSubmission> getSubmission(Assignment assignment, Student student) {
        return submissionRepository.findByAssignmentAndStudent(assignment, student);
    }

    @Transactional
    public AssignmentSubmission gradeSubmission(@NonNull Long submissionId, Double grade, String feedback,
            String grader) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada"));
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        submission.setGradedAt(java.time.LocalDateTime.now());
        submission.setGradedBy(grader);
        return submissionRepository.save(submission);
    }
}
