package com.school.academic.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.repository.AcademicPeriodRepository;
import com.school.core.service.AuditService;

@Service
@Transactional
public class AcademicPeriodService {

	private final AcademicPeriodRepository periodRepository;
	private final AuditService auditService;

	public AcademicPeriodService(AcademicPeriodRepository periodRepository,
			AuditService auditService) {
		this.periodRepository = periodRepository;
		this.auditService = auditService;
	}

	@Transactional(readOnly = true)
	public List<AcademicPeriod> findAll() {
		return periodRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<AcademicPeriod> findById(@NonNull Long id) {
		return periodRepository.findById(id);
	}

	public AcademicPeriod save(@NonNull AcademicPeriod period) {
		AcademicPeriod saved = periodRepository.save(period);
		auditService.logGenericAction("SAVE_ACADEMIC_PERIOD",
				"Periodo académico guardado: " + saved.getCode(),
				getCurrentUser());
		return saved;
	}

	public void deleteById(@NonNull Long id) {
		AcademicPeriod period = periodRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ID de periodo invalido: " + id));
		auditService.logGenericAction("DELETE_ACADEMIC_PERIOD",
				"Periodo académico eliminado: " + period.getCode(),
				getCurrentUser());
		periodRepository.deleteById(id);
	}

	@NonNull
	private String getCurrentUser() {
		org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		if (auth != null && auth.getName() != null) {
			return auth.getName();
		}
		return "system";
	}
}
