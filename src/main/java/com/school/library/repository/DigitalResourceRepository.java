package com.school.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.library.entity.DigitalResource;

@Repository
public interface DigitalResourceRepository extends JpaRepository<DigitalResource, Long> {
    List<DigitalResource> findByCategoryAndDeletedFalse(String category);

    List<DigitalResource> findByDeletedFalse();
}
