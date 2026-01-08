package com.school.academic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.academic.entity.Justification;

@Repository
public interface JustificationRepository extends JpaRepository<Justification, Long> {

}
