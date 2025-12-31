package com.school.core.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.school.core.entity.Parent;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUserId(Long userId);

    @Query("SELECT p FROM Parent p LEFT JOIN FETCH p.children WHERE p.id = :id")
    Optional<Parent> findByIdWithChildren(@Param("id") Long id);

    @Query("SELECT p FROM Parent p WHERE p.dni = :dni")
    Optional<Parent> findByDni(@Param("dni") String dni);

    @Query("SELECT p FROM Parent p WHERE p.email = :email")
    Optional<Parent> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM Parent p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Parent> findByNameContaining(@Param("name") String name, Pageable pageable);

    org.springframework.data.domain.Page<Parent> findByDeletedFalse(org.springframework.data.domain.Pageable pageable);

}