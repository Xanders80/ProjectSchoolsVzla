package com.school.admin.service;

import com.school.admin.entity.Staff;
import com.school.admin.repository.StaffRepository;
import com.school.core.enums.Role;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
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
        staffRepository.deleteById(id);
    }

    public long countStaff() {
        return staffRepository.count();
    }

    public long countTeachers() {
        return staffRepository.countByJobTitle(Role.TEACHER);
    }
}
