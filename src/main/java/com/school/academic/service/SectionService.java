package com.school.academic.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Section;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.EnrollmentRepository;
import com.school.academic.repository.SectionRepository;
import com.school.core.service.AuditService;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AuditService auditService;

    public SectionService(SectionRepository sectionRepository,
                         EnrollmentRepository enrollmentRepository,
                         AttendanceRepository attendanceRepository,
                         AuditService auditService) {
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Section> getAllActiveSections(@NonNull Pageable pageable) {
        return sectionRepository.findAllActive(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Section> getSectionById(@NonNull Long id) {
        return sectionRepository.findByIdAndNotDeleted(id);
    }

    public Section saveSection(@NonNull Section section) {
        return sectionRepository.save(section);
    }

    public void deleteSection(@NonNull Long id) {
        Section section = sectionRepository.findByIdAndNotDeleted(id)
            .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));

        // Validar dependencias
        validateSectionDependencies(id);

        // Soft delete
        section.setDeleted(true);
        section.setDeletedAt(LocalDateTime.now());
        section.setDeletedBy(getCurrentUser());
        
        sectionRepository.save(section);
        
        // Auditoría
        auditService.logSectionDeletion(id, getCurrentUser());
    }

    public void hardDeleteSection(@NonNull Long id) {
        validateSectionDependencies(id);
        sectionRepository.deleteById(id);
        auditService.logSectionDeletion(id, getCurrentUser());
    }

    private void validateSectionDependencies(@NonNull Long sectionId) {
        long enrollmentCount = enrollmentRepository.countBySectionId(sectionId);
        if (enrollmentCount > 0) {
            throw new IllegalStateException(
                String.format("No se puede eliminar la sección. Tiene %d estudiante(s) matriculado(s)", enrollmentCount));
        }

        long attendanceCount = attendanceRepository.countBySectionId(sectionId);
        if (attendanceCount > 0) {
            throw new IllegalStateException(
                String.format("No se puede eliminar la sección. Tiene %d registro(s) de asistencia", attendanceCount));
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}