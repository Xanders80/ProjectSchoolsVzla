package com.school.academic.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Enrollment;
import com.school.academic.entity.Student;
import com.school.academic.repository.EnrollmentRepository;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

	private final EnrollmentRepository enrollmentRepository;

	public EnrollmentService(EnrollmentRepository enrollmentRepository) {
		this.enrollmentRepository = enrollmentRepository;
	}

	public List<Enrollment> findBySectionId(@NonNull Long sectionId) {
		return enrollmentRepository.findBySectionId(sectionId);
	}

	public List<Enrollment> findByStudentId(@NonNull Long studentId) {
		return enrollmentRepository.findByStudentId(studentId);
	}

	public List<Student> findStudentsNotEnrolledInPeriod(@NonNull Long periodId) {
		return enrollmentRepository.findStudentsNotEnrolledInPeriod(periodId);
	}

	public boolean existsBySectionId(@NonNull Long sectionId) {
		return enrollmentRepository.existsBySectionId(sectionId);
	}

	@Transactional
	public Enrollment save(@NonNull Enrollment enrollment) {
		return enrollmentRepository.save(enrollment);
	}

	@Transactional
	public void deleteByStudentId(@NonNull Long studentId) {
		enrollmentRepository.deleteByStudentId(studentId);
	}
}
