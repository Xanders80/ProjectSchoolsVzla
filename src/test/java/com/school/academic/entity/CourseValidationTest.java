package com.school.academic.entity;

import com.school.academic.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenCodeIsTooLong_thenValidationFails() {
        Course course = new Course();
        course.setCode("VERYLONGCODE123"); // 15 characters
        course.setName("Math 101");
        course.setCredits(3);
        course.setGradeLevel(1);

        Set<ConstraintViolation<Course>> violations = validator.validate(course, ValidationGroups.Create.class);

        assertFalse(violations.isEmpty(), "Validation should fail for code length > 10");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")),
                "There should be a violation for the 'code' field");
    }

    @Test
    void whenCodeIsValid_thenValidationSucceeds() {
        Course course = new Course();
        course.setCode("MATH101"); // 7 characters
        course.setName("Math 101");
        course.setCredits(3);
        course.setGradeLevel(1);

        Set<ConstraintViolation<Course>> violations = validator.validate(course, ValidationGroups.Create.class);

        assertTrue(violations.isEmpty(), "Validation should succeed for code length <= 10");
    }
}
