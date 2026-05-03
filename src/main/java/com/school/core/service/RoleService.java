package com.school.core.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.core.entity.Role;
import com.school.core.repository.RoleRepository;

@Service
@Transactional
public class RoleService {

	private final RoleRepository roleRepository;

	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Transactional(readOnly = true)
	public Page<Role> findByDeletedFalse(@NonNull Pageable pageable) {
		return roleRepository.findByDeletedFalse(pageable);
	}

	@Transactional(readOnly = true)
	public Optional<Role> findById(@NonNull Long id) {
		return roleRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Optional<Role> findByName(@NonNull String name) {
		return roleRepository.findByName(name);
	}

	public Role save(@NonNull Role role) {
		return roleRepository.save(role);
	}

	public void softDelete(@NonNull Long id, @NonNull String deletedBy) {
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
		role.setDeleted(true);
		role.setDeletedAt(LocalDateTime.now());
		role.setDeletedBy(deletedBy);
		roleRepository.save(role);
	}
}
