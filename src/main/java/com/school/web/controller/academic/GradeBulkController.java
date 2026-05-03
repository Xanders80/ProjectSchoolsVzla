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
import com.school.academic.service.AcademicPeriodService;
import com.school.academic.service.CourseService;
import com.school.academic.service.EnrollmentService;
import com.school.academic.service.GradeService;
import com.school.academic.service.SectionService;

@Controller
@RequestMapping("/grades/bulk")
public class GradeBulkController {

	private static final String MSG_SUCCESS = "successMessage";
	private static final String MSG_ERROR = "errorMessage";

	private final GradeService gradeService;
	private final CourseService courseService;
	private final SectionService sectionService;
	private final AcademicPeriodService academicPeriodService;
	private final EnrollmentService enrollmentService;

	public GradeBulkController(GradeService gradeService,
			CourseService courseService,
			SectionService sectionService,
			AcademicPeriodService academicPeriodService,
			EnrollmentService enrollmentService) {
		this.gradeService = gradeService;
		this.courseService = courseService;
		this.sectionService = sectionService;
		this.academicPeriodService = academicPeriodService;
		this.enrollmentService = enrollmentService;
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
			sectionService.getSectionById(sectionId)
					.orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));

			courseService.getCourseById(courseId)
					.orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

			academicPeriodService.findById(periodId)
					.orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));

			List<Enrollment> enrollments = enrollmentService.findBySectionId(sectionId);
			List<Student> students = enrollments.stream().map(Enrollment::getStudent)
					.collect(Collectors.toList());

			List<Grade> existingGrades = gradeService.findByCourseId(courseId).stream()
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
			redirectAttributes.addFlashAttribute(MSG_ERROR, "IDs de curso y periodo son obligatorios");
			return "redirect:/grades/bulk";
		}

		try {
			Course course = courseService.getCourseById(courseId)
					.orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
			AcademicPeriod period = academicPeriodService.findById(periodId)
					.orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));

			gradeService.saveBulkGrades(bulkDto, course, period);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Calificaciones guardadas exitosamente");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, e.getMessage());
			return "redirect:/grades/bulk";
		}

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
		model.addAttribute("periods", academicPeriodService.findAll());
		model.addAttribute("evaluationTypes", EvaluationType.values());
	}
}
