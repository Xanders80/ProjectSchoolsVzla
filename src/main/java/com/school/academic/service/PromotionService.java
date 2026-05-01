package com.school.academic.service;

import java.time.LocalDateTime;
import java.util.List;

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
@Transactional
public class PromotionService {

	private final PromotionRepository promotionRepository;
	private final GradeRepository gradeRepository;
	private final StudentRepository studentRepository;
	private final AuditService auditService;

	public PromotionService(PromotionRepository promotionRepository,
			GradeRepository gradeRepository,
			StudentRepository studentRepository,
			AuditService auditService) {
		this.promotionRepository = promotionRepository;
		this.gradeRepository = gradeRepository;
		this.studentRepository = studentRepository;
		this.auditService = auditService;
	}

	@Transactional(readOnly = true)
	public List<Promotion> findAll() {
		return promotionRepository.findAll();
	}

	public Promotion save(Promotion promotion) {
		return promotionRepository.save(promotion);
	}

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
		if (average >= 70.0 && failedCourses <= 2) {
			status = "PROMOTED";
		} else if (failedCourses > 4) {
			status = "RETAINED";
		} else {
			status = "PENDING_RECOVERY";
		}

		return new PromotionResult(studentId, status, average, (int) failedCourses);
	}

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
