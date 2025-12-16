package com.school.web.controller.academic;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.school.academic.entity.Grade;
import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final AcademicService academicService;

    public ReportController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping("/cards")
    public String reportCardSearch(@RequestParam(required = false) Long studentId, Model model) {
        // Populate student dropdown
        model.addAttribute("students", academicService.getAllStudents());

        if (studentId != null) {
            Student student = academicService.getStudentById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));

            List<Grade> grades = academicService.getGradesByStudent(studentId);

            // Calculate simple average
            double average = grades.stream()
                    .mapToDouble(Grade::getScore)
                    .average()
                    .orElse(0.0);

            model.addAttribute("selectedStudent", student);
            model.addAttribute("grades", grades);
            model.addAttribute("average", Math.round(average * 100.0) / 100.0); // 2 decimals
        }

        return "academic/report-card";
    }
}
