package com.school.academic.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.schedule.entity.ScheduleEntry;
import com.school.schedule.repository.ScheduleRepository;

@Service
public class BatchScheduleService {

	private static final Logger logger = LoggerFactory.getLogger(BatchScheduleService.class);
	private final ScheduleRepository scheduleRepository;

	public BatchScheduleService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}

	@Async
	public CompletableFuture<Boolean> processBulkSchedules(List<ScheduleEntry> entries) {
		logger.info("Iniciando procesamiento batch de {} horarios", entries.size());
		return doProcessBulkSchedules(entries);
	}

	@Transactional
	public CompletableFuture<Boolean> doProcessBulkSchedules(List<ScheduleEntry> entries) {
		try {
			for (ScheduleEntry entry : entries) {
				validateConflicts(entry);
				scheduleRepository.save(entry);
			}
			logger.info("Procesamiento batch completado exitosamente");
			return CompletableFuture.completedFuture(true);
		} catch (Exception e) {
			logger.error("Error en procesamiento batch: {}", e.getMessage(), e);
			throw new RuntimeException("Batch processing failed", e);
		}
	}

	private void validateConflicts(ScheduleEntry entry) {
	}
}
