package com.school.academic.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.school.academic.entity.AcademicPeriod;
import com.school.academic.repository.AcademicPeriodRepository;

@Configuration
public class AcademicDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(AcademicDataInitializer.class);

    @Bean
    CommandLineRunner initAcademicPeriods(AcademicPeriodRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                AcademicPeriod q1 = new AcademicPeriod();
                q1.setCode("2024-Q1");
                q1.setName("Trimestre 1 (Oct-Dic)");
                q1.setStartDate(LocalDate.of(2024, 10, 1));
                q1.setEndDate(LocalDate.of(2024, 12, 31));
                q1.setActive(true);

                AcademicPeriod q2 = new AcademicPeriod();
                q2.setCode("2025-Q2");
                q2.setName("Trimestre 2 (Ene-Mar)");
                q2.setStartDate(LocalDate.of(2025, 1, 1));
                q2.setEndDate(LocalDate.of(2025, 3, 31));
                q2.setActive(true);

                AcademicPeriod q3 = new AcademicPeriod();
                q3.setCode("2025-Q3");
                q3.setName("Trimestre 3 (Abr-Jul)");
                q3.setStartDate(LocalDate.of(2025, 4, 1));
                q3.setEndDate(LocalDate.of(2025, 7, 31));
                q3.setActive(true);

                repository.saveAll(Arrays.asList(q1, q2, q3));
                log.info(">> Academic periods initialized (Oct-Jul schedule)");
            }
        };
    }
}
