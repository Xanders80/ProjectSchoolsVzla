package com.school.web.controller.academic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.dto.GradeBulkEntryDTO;
import com.school.academic.entity.AcademicPeriod;
import com.school.academic.entity.Course;
import com.school.academic.entity.Enrollment;
import com.school.academic.entity.Grade;
import com.school.academic.entity.Student;
import com.school.academic.enums.EvaluationType;
import com.school.academic.repository.AcademicPeriodRepository;
import com.school.academic.repository.EnrollmentRepository;
import com.school.academic.repository.GradeRepository;
import com.school.academic.service.CourseService;
import com.school.academic.service.GradeService;
import com.school.academic.service.SectionService;

@Controller
@RequestMapping("/grades/bulk")
public class GradeBulkController {

        private final GradeService gradeService;
        private final CourseService courseService;
        private final SectionService sectionService;
        private final AcademicPeriodRepository academicPeriodRepository;
        private final GradeRepository gradeRepository;
        private final EnrollmentRepository enrollmentRepository;

        public GradeBulkController(GradeService gradeService,
                        CourseService courseService,
                        SectionService sectionService,
                        AcademicPeriodRepository academicPeriodRepository,
                        GradeRepository gradeRepository,
                        EnrollmentRepository enrollmentRepository) {
                this.gradeService = gradeService;
                this.courseService = courseService;
                this.sectionService = sectionService;
                this.academicPeriodRepository = academicPeriodRepository;
                this.gradeRepository = gradeRepository;
                this.enrollmentRepository = enrollmentRepository;
        }

        @GetMapping
        public String showBulkForm(Model model,
                        @RequestParam(required = false) Long courseId,
                        @RequestParam(required = false) Long sectionId,
                        @RequestParam(required = false) Long periodId,
                        @RequestParam(required = false) EvaluationType evaluationType) {

                populateCommonData(model);

                GradeBulkEntryDTO bulkDto = new GradeBulkEntryDTO();
                bulkDto.setCourseId(courseId);
                bulkDto.setSectionId(sectionId);
                bulkDto.setPeriodId(periodId);
                bulkDto.setEvaluationType(evaluationType);

                if (sectionId != null && courseId != null && periodId != null && evaluationType != null) {
                        // Validate IDs
                        sectionService.getSectionById(sectionId)
                                        .orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));

                        courseService.getCourseById(courseId)
                                        .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

                        academicPeriodRepository.findById(periodId)
                                        .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));

                        // Get students in this section via Enrollments
                        List<Enrollment> enrollments = enrollmentRepository.findBySectionId(sectionId);
                        List<Student> students = enrollments.stream().map(Enrollment::getStudent)
                                        .collect(Collectors.toList());

                        // Fetch existing grades for this course/period/evaluationType to pre-populate
                        List<Grade> existingGrades = gradeRepository.findByCourseId(courseId).stream()
                                        .filter(g -> g.getPeriod().getId().equals(periodId)
                                                        && g.getEvaluationType() == evaluationType)
                                        .collect(Collectors.toList());

                        Map<Long, Double> gradeMap = existingGrades.stream()
                                        .collect(Collectors.toMap(g -> g.getStudent().getId(), Grade::getScore,
                                                        (a, b) -> a));

                        List<GradeBulkEntryDTO.StudentGradeDTO> studentGrades = students.stream().map(s -> {
                                GradeBulkEntryDTO.StudentGradeDTO sg = new GradeBulkEntryDTO.StudentGradeDTO();
                                sg.setStudentId(s.getId());
                                sg.setStudentName(s.getFirstName() + " " + s.getLastName());
                                sg.setScore(gradeMap.get(s.getId()));
                                return sg;
                        }).collect(Collectors.toList());

                        bulkDto.setStudentGrades(studentGrades);

                        // Log for debugging
                        org.slf4j.LoggerFactory.getLogger(GradeBulkController.class)
                                        .info("Loaded {} students for section {} and course {}", students.size(),
                                                        sectionId, courseId);

                        if (students.isEmpty()) {
                                model.addAttribute("info", "No se encontraron alumnos matriculados en esta sección.");
                        }
                } else {
                        bulkDto.setStudentGrades(new ArrayList<>());
                }

                model.addAttribute("bulkDto", bulkDto);
                return "academic/grade-bulk-form";
        }

        @PostMapping("/save")
        public String saveBulkGrades(@ModelAttribute GradeBulkEntryDTO bulkDto, RedirectAttributes redirectAttributes) {
                Long courseId = bulkDto.getCourseId();
                Long periodId = bulkDto.getPeriodId();

                if (courseId == null || periodId == null) {
                        throw new IllegalArgumentException("IDs de curso y periodo son obligatorios");
                }

                Course course = courseService.getCourseById(courseId)
                                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
                AcademicPeriod period = academicPeriodRepository.findById(periodId)
                                .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));

                gradeService.saveBulkGrades(bulkDto, course, period);

                redirectAttributes.addFlashAttribute("success", "Calificaciones guardadas exitosamente");
                return "redirect:/grades/bulk?courseId=" + bulkDto.getCourseId() +
                                "&sectionId=" + bulkDto.getSectionId() +
                                "&periodId=" + bulkDto.getPeriodId() +
                                "&evaluationType=" + bulkDto.getEvaluationType();
        }

        private void populateCommonData(Model model) {
                model.addAttribute("courses", courseService.getAllActiveCourses());
                model.addAttribute("sections",
                                sectionService.getAllActiveSections(org.springframework.data.domain.Pageable.unpaged())
                                                .getContent());
                model.addAttribute("periods", academicPeriodRepository.findAll());
                model.addAttribute("evaluationTypes", EvaluationType.values());
        }
}
