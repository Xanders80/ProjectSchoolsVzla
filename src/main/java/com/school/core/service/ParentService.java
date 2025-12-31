package com.school.core.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.core.entity.Parent;
import com.school.core.repository.ParentRepository;

@Service
@Transactional
public class ParentService {

    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public Page<Parent> getAllParents(@NonNull Pageable pageable) {
        return parentRepository.findByDeletedFalse(pageable);
    }

    public Optional<Parent> getParentById(@NonNull Long id) {
        return parentRepository.findByIdWithChildren(id);
    }

    public Parent saveParent(@NonNull Parent parent) {
        // Verificar DNI único
        if (parent.getDni() != null) {
            parentRepository.findByDni(parent.getDni())
                    .ifPresent(existing -> {
                        if (parent.getId() == null || !existing.getId().equals(parent.getId())) {
                            throw new IllegalArgumentException("El DNI ya está registrado para otro representante.");
                        }
                    });
        }
        return parentRepository.save(parent);
    }

    public void deleteParent(@NonNull Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Representante no encontrado"));

        parent.setDeleted(true);
        parent.setDeletedAt(java.time.LocalDateTime.now());
        parent.setDeletedBy(getCurrentUser());

        parentRepository.save(parent);
    }

    @NonNull
    private String getCurrentUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null) {
            String name = auth.getName();
            if (name != null) {
                return name;
            }
        }
        return "system";
    }

    public Optional<Parent> findByDni(String dni) {
        return parentRepository.findByDni(dni);
    }

    public Optional<Parent> findByEmail(String email) {
        return parentRepository.findByEmail(email);
    }

    public Page<Parent> searchByName(String name, Pageable pageable) {
        return parentRepository.findByNameContaining(name, pageable);
    }

    public Optional<Parent> getParentByUserId(Long userId) {
        return parentRepository.findByUserId(userId);
    }
}