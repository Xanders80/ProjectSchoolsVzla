package com.school.academic.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.school.academic.entity.AdmissionExam;

@DataJpaTest
public class AdmissionExamRepositoryTest {

    @Autowired
    private AdmissionExamRepository admissionExamRepository;

    @Test
    @DisplayName("Should save and find AdmissionExam by DNI")
    void testSaveAndFind() {
        // Arrange
	AdmissionExam exam = new AdmissionExam();
		exam.setApplicantDni("12345678");
		exam.setApplicantName("Juan Perez");
		exam.setApplicantEmail("juan@test.com");
		exam.setExamDate(LocalDate.now());
		exam.setStatus("PENDING");

        // Act
        admissionExamRepository.save(exam);
        Optional<AdmissionExam> found = admissionExamRepository.findByApplicantDni("12345678");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getApplicantName()).isEqualTo("Juan Perez");
    }
}
