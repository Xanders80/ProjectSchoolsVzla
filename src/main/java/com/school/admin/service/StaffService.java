package com.school.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.repository.SectionRepository;
import com.school.admin.entity.Staff;
import com.school.admin.repository.StaffRepository;
import com.school.core.enums.Role;
import com.school.core.service.AuditService;

@Service
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;
    private final SectionRepository sectionRepository;
    private final AuditService auditService;

    public StaffService(StaffRepository staffRepository,
            SectionRepository sectionRepository,
            AuditService auditService) {
        this.staffRepository = staffRepository;
        this.sectionRepository = sectionRepository;
        this.auditService = auditService;
    }

    public org.springframework.data.domain.Page<Staff> getAllStaff(
            @NonNull org.springframework.data.domain.Pageable pageable) {
        return staffRepository.findAll(pageable);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public org.springframework.data.domain.Page<Staff> getTeachers(org.springframework.data.domain.Pageable pageable) {
        return staffRepository.findByJobTitle(Role.TEACHER, pageable);
    }

    public List<Staff> getAllTeachers() {
        return staffRepository.findByJobTitle(Role.TEACHER);
    }

    public Optional<Staff> getStaffById(@NonNull Long id) {
        return staffRepository.findById(id);
    }

    public Staff saveStaff(@NonNull Staff staff) {
        return staffRepository.save(staff);
    }

    public void deleteStaff(@NonNull Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));

        validateStaffDependencies(id);

        staff.setDeleted(true);
        staff.setDeletedAt(LocalDateTime.now());
        staff.setDeletedBy(getCurrentUser());

        staffRepository.save(staff);
        auditService.logStaffDeletion(id, getCurrentUser());
    }

    private void validateStaffDependencies(@NonNull Long staffId) {
        // Verificar si el staff es profesor de alguna sección
        long sectionCount = sectionRepository.findAll().stream()
                .filter(s -> s.getTeacher() != null && s.getTeacher().getId().equals(staffId))
                .count();

        if (sectionCount > 0) {
            throw new IllegalStateException(
                    String.format("No se puede eliminar el personal. Es profesor de %d sección(es)", sectionCount));
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public long countStaff() {
        return staffRepository.count();
    }

    public long countTeachers() {
        return staffRepository.countByJobTitle(Role.TEACHER);
    }

    public Optional<Staff> findByEmail(String email) {
        return staffRepository.findByEmail(email);
    }

    public Optional<Staff> getStaffByUserId(Long userId) {
        return staffRepository.findByUserId(userId);
    }
}
