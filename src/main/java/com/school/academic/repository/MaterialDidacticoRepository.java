package com.school.academic.repository;

import com.school.academic.entity.MaterialDidactico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialDidacticoRepository extends JpaRepository<MaterialDidactico, Long> {
}
