package com.school.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.school.academic.service.AcademicService;
import com.school.academic.service.CourseService;
import com.school.admin.service.StaffService;

@Controller
public class HomeController {

	private final AcademicService academicService;
	private final StaffService staffService;
	private final CourseService courseService;

	public HomeController(AcademicService academicService, StaffService staffService,
			CourseService courseService) {
		this.academicService = academicService;
		this.staffService = staffService;
		this.courseService = courseService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("totalStudents", academicService.countStudents());
		model.addAttribute("totalTeachers", staffService.countTeachers());
		model.addAttribute("activeClasses", courseService.countActiveCourses());
		model.addAttribute("totalStaff", staffService.countStaff());

		java.time.LocalDate now = java.time.LocalDate.now();
		int month = now.getMonthValue();
		int year = now.getYear();

		String semester;
		String academicYear;

		if (month >= 8 && month <= 12) {
			semester = "Otoño " + year;
			academicYear = year + "-" + (year + 1);
		} else if (month >= 1 && month <= 5) {
			semester = "Primavera " + year;
			academicYear = (year - 1) + "-" + year;
		} else {
			semester = "Verano " + year;
			academicYear = (year - 1) + "-" + year;
		}

		model.addAttribute("currentSemester", semester);
		model.addAttribute("academicYear", academicYear);
		model.addAttribute("systemStatus", "Activo");

		java.time.LocalDateTime lastBackupTime = java.time.LocalDateTime.now()
				.minusDays(1)
				.withHour(23)
				.withMinute(0)
				.withSecond(0);

		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
				.ofPattern("MMMM dd, yyyy HH:mm");
		model.addAttribute("lastBackup", lastBackupTime.format(formatter));

		return "index";
	}
}
