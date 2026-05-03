package com.school.academic.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Grade;
import com.school.academic.entity.Promotion;
import com.school.academic.entity.Student;
import com.school.academic.repository.GradeRepository;
import com.school.academic.repository.PromotionRepository;
import com.school.academic.repository.StudentRepository;
import com.school.core.service.AuditService;

@Service
@Transactional(readOnly = true)
public class PromotionService {

	private final PromotionRepository promotionRepository;
	private final GradeRepository gradeRepository;
	private final StudentRepository studentRepository;
	private final AuditService auditService;

	@Value("${app.promotion.average-threshold:70.0}")
	private double averageThreshold;

	@Value("${app.promotion.max-failed-courses:2}")
	private int maxFailedCourses;

	@Value("${app.promotion.max-failed-courses-retain:4}")
	private int maxFailedCoursesRetain;

	public PromotionService(PromotionRepository promotionRepository,
			GradeRepository gradeRepository,
			StudentRepository studentRepository,
			AuditService auditService) {
		this.promotionRepository = promotionRepository;
		this.gradeRepository = gradeRepository;
		this.studentRepository = studentRepository;
		this.auditService = auditService;
	}

	public List<Promotion> findAll() {
		return promotionRepository.findAll();
	}

	@Transactional
	public Promotion save(Promotion promotion) {
		return promotionRepository.save(promotion);
	}

	@Transactional
	public void delete(Long id) {
		Promotion promotion = promotionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Promocion no encontrada"));
		promotion.setDeleted(true);
		promotion.setDeletedAt(LocalDateTime.now());
		promotionRepository.save(promotion);
	}

	public PromotionResult evaluatePromotion(@NonNull Long studentId, @NonNull Long periodId) {
		List<Grade> grades = gradeRepository.findByStudentIdAndDeletedFalseOrderByDateDesc(studentId).stream()
				.filter(g -> g.getPeriod() != null && g.getPeriod().getId() != null
						&& g.getPeriod().getId().equals(periodId))
				.toList();

		if (grades.isEmpty()) {
			return new PromotionResult(studentId, "NO_DATA", 0.0, 0);
		}

		double average = grades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);
		long failedCourses = grades.stream().filter(g -> g.getScore() < 60.0).count();

		String status;
		if (average >= averageThreshold && failedCourses <= maxFailedCourses) {
			status = "PROMOTED";
		} else if (failedCourses > maxFailedCoursesRetain) {
			status = "RETAINED";
		} else {
			status = "PENDING_RECOVERY";
		}

		return new PromotionResult(studentId, status, average, (int) failedCourses);
	}

	@Transactional
	public void processMassPromotion(@NonNull Long periodId) {
		List<Student> activeStudents = studentRepository.findAllActive();

		for (Student student : activeStudents) {
			PromotionResult result = evaluatePromotion(
					java.util.Objects.requireNonNull(student.getId(), "ID de estudiante no puede ser null"), periodId);
			auditService.logGenericAction("ACADEMIC_PROMOTION",
					"El estudiante " + student.getId() + " evaluacion: " + result.getStatus(),
					"system");
		}
	}

	public static class PromotionResult {

		private final Long studentId;
		private final String status;
		private final double average;
		private final int failedCourses;

		public PromotionResult(Long studentId, String status, double average, int failedCourses) {
			this.studentId = studentId;
			this.status = status;
			this.average = average;
			this.failedCourses = failedCourses;
		}

		public Long getStudentId() {
			return studentId;
		}

		public String getStatus() {
			return status;
		}

		public double getAverage() {
			return average;
		}

		public int getFailedCourses() {
			return failedCourses;
		}
	}
}
