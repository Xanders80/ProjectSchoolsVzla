package com.school.hr.repository;

import java.util.List;
import com.school.hr.entity.DisciplinaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaryRecordRepository extends JpaRepository<DisciplinaryRecord, Long> {
    List<DisciplinaryRecord> findByTeacherProfileId(Long teacherProfileId);

    List<DisciplinaryRecord> findByStatus(String status);
}
