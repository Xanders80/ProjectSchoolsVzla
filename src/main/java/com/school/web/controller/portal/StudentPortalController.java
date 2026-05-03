package com.school.web.controller.portal;

import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;
import com.school.core.entity.User;
import com.school.core.service.UserService;
import com.school.schedule.service.ScheduleService;

@Controller
@RequestMapping("/portal/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPortalController {

	private final AcademicService academicService;
	private final UserService userService;
	private final ScheduleService scheduleService;

	public StudentPortalController(AcademicService academicService,
			UserService userService,
			ScheduleService scheduleService) {
		this.academicService = academicService;
		this.userService = userService;
		this.scheduleService = scheduleService;
	}

	@GetMapping
	public String dashboard(Model model) {
		Student student = getCurrentStudent();
		if (student == null) {
			return "redirect:/login";
		}

		model.addAttribute("student", student);

		var grades = academicService.getGradesByStudent(student.getId());
		model.addAttribute("grades", grades);

		DoubleSummaryStatistics stats = grades.stream()
				.mapToDouble(g -> g.getScore())
				.summaryStatistics();
		model.addAttribute("average", stats.getCount() > 0 ? stats.getAverage() : 0.0);

		var attendance = academicService.getAttendanceByStudent(student.getId());
		long totalAttendance = attendance.size();
		long presentCount = attendance.stream()
				.filter(a -> a.getStatus() != null && a.getStatus().name().equals("PRESENT"))
				.count();
		double attendancePercent = totalAttendance > 0 ? (presentCount * 100.0 / totalAttendance) : 0.0;
		model.addAttribute("attendancePercent", attendancePercent);

		model.addAttribute("schedule", scheduleService.getAllSchedules());

		return "portal/student-dashboard";
	}

	private Student getCurrentStudent() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) return null;

		String username = auth.getName();
		User user = userService.findByUsername(username).orElse(null);
		if (user == null) return null;

		return academicService.getStudentByUserId(user.getId()).orElse(null);
	}
}
