package com.school.web.controller.academic;

import com.school.academic.entity.Attendance;
import com.school.academic.entity.Section;
import com.school.academic.entity.Student;
import com.school.academic.enums.AttendanceStatus;
import com.school.academic.service.AcademicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AcademicService academicService;

    public AttendanceController(AcademicService academicService) {
        this.academicService = academicService;
    }

    @GetMapping
    public String attendanceSheet(@RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String dateStr,
            Model model) {
        List<Section> sections = academicService.getAllSections();
        model.addAttribute("sections", sections);

        if (sectionId != null) {
            LocalDate date = (dateStr == null || dateStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(dateStr);
            model.addAttribute("selectedSectionId", sectionId);
            model.addAttribute("selectedDate", date);

            List<Attendance> attendanceList = academicService.getAttendanceBySectionAndDate(sectionId, date);

            // If no records exist, create a template list for all students in the section
            // (MVP: All active students)
            // Note: Ideally, Sections should have enrolled Students. For MVP, we will list
            // ALL students.
            // TODO: Filter students by enrollment in future.

            if (attendanceList.isEmpty()) {
                attendanceList = new ArrayList<>();
                // Fetch students - using unpaged for simplicity in this view
                List<Student> students = academicService.getAllStudents();
                Section section = academicService.getSectionById(sectionId).orElseThrow();

                for (Student s : students) {
                    Attendance a = new Attendance();
                    a.setStudent(s);
                    a.setSection(section);
                    a.setDate(date);
                    a.setStatus(AttendanceStatus.PRESENT); // Default
                    attendanceList.add(a);
                }
            }

            AttendanceWrapper wrapper = new AttendanceWrapper();
            wrapper.setAttendanceList(attendanceList);
            model.addAttribute("wrapper", wrapper);
        }

        return "academic/attendance-sheet";
    }

    @PostMapping("/save")
    public String saveAttendance(@ModelAttribute AttendanceWrapper wrapper, RedirectAttributes redirectAttributes) {
        if (wrapper.getAttendanceList() != null) {
            academicService.saveAttendanceList(wrapper.getAttendanceList());
            redirectAttributes.addFlashAttribute("success", "Asistencia guardada exitosamente");
        }
        // Redirect back to the same sheet view if possible, else root
        return "redirect:/attendance";
    }

    // Wrapper class for binding list in Thymeleaf
    public static class AttendanceWrapper {
        private List<Attendance> attendanceList;

        public List<Attendance> getAttendanceList() {
            return attendanceList;
        }

        public void setAttendanceList(List<Attendance> attendanceList) {
            this.attendanceList = attendanceList;
        }
    }
}
