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
        return parentRepository.findAll(pageable);
    }

    public Optional<Parent> getParentById(@NonNull Long id) {
        return parentRepository.findByIdWithChildren(id);
    }

    public Parent saveParent(@NonNull Parent parent) {
        return parentRepository.save(parent);
    }

    public void deleteParent(@NonNull Long id) {
        parentRepository.deleteById(id);
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