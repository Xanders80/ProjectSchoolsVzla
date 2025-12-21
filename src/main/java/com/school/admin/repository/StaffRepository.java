package com.school.admin.repository;

import com.school.admin.entity.Staff;
import com.school.core.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    org.springframework.data.domain.Page<Staff> findByJobTitle(Role jobTitle,
            org.springframework.data.domain.Pageable pageable);

    java.util.List<Staff> findByJobTitle(Role jobTitle);

    // Kept for backward compatibility if needed, or remove if unused.
    // Since I changed the Service to use the paged version, I might not need the
    // list version unless used elsewhere.
    // Checking usage... The service uses the paged version now.

    long countByJobTitle(Role jobTitle);

    Optional<Staff> findByDni(String dni);

    Optional<Staff> findByEmail(String email);
}
