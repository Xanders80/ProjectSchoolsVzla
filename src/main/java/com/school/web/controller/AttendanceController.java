package com.school.web.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.dto.AttendanceDTO;
import com.school.academic.dto.StudentAttendanceStatsDTO;
import com.school.academic.entity.Section;
import com.school.academic.enums.AttendanceStatus;
import com.school.academic.service.AttendanceService;
import com.school.academic.service.SectionService;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	private final AttendanceService attendanceService;
	private final SectionService sectionService;

	public AttendanceController(AttendanceService attendanceService, SectionService sectionService) {
		this.attendanceService = attendanceService;
		this.sectionService = sectionService;
	}

	@GetMapping
	public String dashboard(Model model) {
		model.addAttribute("sections", sectionService.findAll());
		return "academic/attendance/dashboard";
	}

	@GetMapping("/section/{sectionId}")
	public String takeAttendance(@PathVariable @NonNull Long sectionId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			Model model) {
		if (date == null) {
			date = LocalDate.now();
		}

		Section section = sectionService.findById(sectionId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));

		List<AttendanceDTO> attendanceList = attendanceService.getAttendanceDTOs(sectionId, date);

		model.addAttribute("section", section);
		model.addAttribute("date", date);
		model.addAttribute("attendanceList", attendanceList);
		model.addAttribute("statuses", AttendanceStatus.values());

		return "academic/attendance/daily-register";
	}

	@PostMapping("/save")
	public String saveAttendance(@RequestParam @NonNull Long sectionId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam Map<String, String> allParams,
			RedirectAttributes redirectAttributes) {

		java.util.Map<Long, AttendanceStatus> studentStatuses = new java.util.HashMap<>();
		java.util.Map<Long, String> studentRemarks = new java.util.HashMap<>();

		for (Map.Entry<String, String> entry : allParams.entrySet()) {
			if (entry.getKey().startsWith("student_")) {
				Long studentId = Long.parseLong(entry.getKey().replace("student_", ""));
				AttendanceStatus status = AttendanceStatus.valueOf(entry.getValue());
				studentStatuses.put(studentId, status);
			} else if (entry.getKey().startsWith("remarks_")) {
				Long studentId = Long.parseLong(entry.getKey().replace("remarks_", ""));
				studentRemarks.put(studentId, entry.getValue());
			}
		}

		attendanceService.saveBatchAttendance(sectionId, date, studentStatuses, studentRemarks);

		redirectAttributes.addFlashAttribute("successMessage", "Asistencia guardada correctamente.");
		return "redirect:/attendance/section/" + sectionId + "?date=" + date;
	}

	@GetMapping("/report")
	public String report(@RequestParam(required = false) Long sectionId,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Integer year,
			Model model) {

		if (month == null)
			month = LocalDate.now().getMonthValue();
		if (year == null)
			year = LocalDate.now().getYear();

		model.addAttribute("sections", sectionService.findAll());
		model.addAttribute("sectionId", sectionId);
		model.addAttribute("month", month);
		model.addAttribute("year", year);

		java.util.List<java.util.Map<String, Object>> months = new java.util.ArrayList<>();
		String[] monthNames = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto",
				"Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		for (int i = 0; i < 12; i++) {
			java.util.Map<String, Object> m = new java.util.HashMap<>();
			m.put("value", i + 1);
			m.put("name", monthNames[i]);
			months.add(m);
		}
		model.addAttribute("months", months);

		if (sectionId != null) {
			List<StudentAttendanceStatsDTO> studentStats = attendanceService.getSectionStats(sectionId, month, year);
			model.addAttribute("studentStats", studentStats);

			long totalPresent = studentStats.stream().mapToLong(StudentAttendanceStatsDTO::getPresentCount).sum();
			long totalLate = studentStats.stream().mapToLong(StudentAttendanceStatsDTO::getLateCount).sum();
			long totalAbsent = studentStats.stream().mapToLong(StudentAttendanceStatsDTO::getAbsentCount).sum();
			long totalExcused = studentStats.stream().mapToLong(StudentAttendanceStatsDTO::getExcusedCount).sum();

			model.addAttribute("totalPresent", totalPresent);
			model.addAttribute("totalLate", totalLate);
			model.addAttribute("totalAbsent", totalAbsent);
			model.addAttribute("totalExcused", totalExcused);
		}

		return "academic/attendance/report";
	}
}
