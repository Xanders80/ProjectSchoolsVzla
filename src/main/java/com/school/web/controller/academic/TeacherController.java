package com.school.web.controller.academic;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.TeacherProfile;
import com.school.academic.service.TeacherManagementService;
import com.school.academic.service.TeacherProfileService;
import com.school.admin.entity.Staff;
import com.school.admin.service.StaffService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/academic/teachers")
public class TeacherController {

    private static final String PROFILE_NOT_FOUND_MSG = "Perfil o profesor no encontrado";
    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_TEACHERS = "redirect:/academic/teachers";

    private final StaffService staffService;
    private final TeacherProfileService teacherProfileService;
    private final TeacherManagementService managementService;

    public TeacherController(StaffService staffService,
            TeacherProfileService teacherProfileService,
            TeacherManagementService managementService) {
        this.staffService = staffService;
        this.teacherProfileService = teacherProfileService;
        this.managementService = managementService;
    }

    @GetMapping
    public String listTeachers(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        Page<Staff> teachers = staffService.getTeachers(pageable);

        model.addAttribute("teachers", teachers);
        return "academic/teacher-list";
    }

    @org.springframework.web.bind.annotation.ModelAttribute("profile")
    public TeacherProfile getProfile(@PathVariable(required = false) Long staffId) {
        if (staffId != null) {
            return teacherProfileService.getProfileByStaffId(staffId).orElse(new TeacherProfile());
        }
        return new TeacherProfile();
    }

    @GetMapping("/{staffId}/profile")
    public String editProfile(@PathVariable @NonNull Long staffId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Staff> staffOpt = staffService.getStaffById(staffId);
        if (staffOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, PROFILE_NOT_FOUND_MSG);
            return REDIRECT_TEACHERS;
        }

        Staff staff = staffOpt.get();
        Optional<TeacherProfile> profileOpt = teacherProfileService.getProfileByStaffId(staffId);
        TeacherProfile profile = profileOpt.orElseGet(() -> {
            TeacherProfile newProfile = new TeacherProfile();
            newProfile.setStaff(staff);
            return newProfile;
        });

        // Initialize seniority date from hire date if empty
        if (profile.getSeniorityDate() == null && staff.getHireDate() != null) {
            profile.setSeniorityDate(staff.getHireDate());
        }

        model.addAttribute("staff", staff);
        model.addAttribute("profile", profile);

        model.addAttribute("developments", managementService.getTeacherFiles(profile.getId()));
        model.addAttribute("disciplinaryRecords", managementService.getTeacherDisciplinaryHistory(profile.getId()));
        model.addAttribute("evaluations", managementService.getTeacherEvaluations(profile.getId()));
        model.addAttribute("academicPeriods", managementService.getAllAcademicPeriods());

        return "academic/teacher-profile";
    }

    @PostMapping("/profile/{profileId}/evaluate")
    public String postEvaluation(@PathVariable @NonNull Long profileId,
            @RequestParam Long periodId,
            @RequestParam Double pedagogy,
            @RequestParam Double seniority,
            @RequestParam Double formation,
            @RequestParam(required = false) String comments,
            RedirectAttributes redirectAttributes) {
        try {
            managementService.evaluateTeacher(profileId, periodId, pedagogy, seniority, formation, comments);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Evaluación registrada exitosamente");

            var profileOpt = teacherProfileService.getProfileById(profileId);
            if (profileOpt.isEmpty() || profileOpt.get().getStaff() == null) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, PROFILE_NOT_FOUND_MSG);
                return REDIRECT_TEACHERS;
            }
            return "redirect:/academic/teachers/" + profileOpt.get().getStaff().getId() + "/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al registrar evaluación: " + e.getMessage());
            return REDIRECT_TEACHERS;
        }
    }

    @PostMapping("/profile/{profileId}/evaluate/{evaluationId}/sign")
    public String signEvaluation(@PathVariable Long profileId,
            @PathVariable Long evaluationId,
            @RequestParam String signerName,
            RedirectAttributes redirectAttributes) {
        try {
            managementService.signEvaluation(evaluationId, signerName);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Evaluación firmada digitalmente");

            var profileOpt = teacherProfileService.getProfileById(profileId);
            if (profileOpt.isEmpty() || profileOpt.get().getStaff() == null) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, PROFILE_NOT_FOUND_MSG);
			return REDIRECT_TEACHERS;
		}
		return "redirect:/academic/teachers/" + profileOpt.get().getStaff().getId() + "/profile";
	} catch (Exception e) {
		redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al firmar evaluación: " + e.getMessage());
            return REDIRECT_TEACHERS;
        }
    }

    @PostMapping("/profile/{profileId}/discipline/report")
    public String reportInfraction(@PathVariable Long profileId,
            @RequestParam String infractionType,
            @RequestParam String description,
            @RequestParam String incidentDate,
            RedirectAttributes redirectAttributes) {
        try {
            managementService.createDisciplinaryRecord(
                    profileId,
                    infractionType,
                    description,
                    java.time.LocalDateTime.parse(incidentDate));
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Incidente disciplinario reportado");

            var profileOpt = teacherProfileService.getProfileById(profileId);
            if (profileOpt.isEmpty() || profileOpt.get().getStaff() == null) {
			redirectAttributes.addFlashAttribute(ERROR_MESSAGE, PROFILE_NOT_FOUND_MSG);
			return REDIRECT_TEACHERS;
		}
		return "redirect:/academic/teachers/" + profileOpt.get().getStaff().getId() + "/profile";
	} catch (Exception e) {
		redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al reportar incidente: " + e.getMessage());
            return REDIRECT_TEACHERS;
        }
    }

    @PostMapping("/profile/{profileId}/discipline/{recordId}/resolve")
    public String resolveInfraction(@PathVariable Long profileId,
            @PathVariable Long recordId,
            @RequestParam String resolution,
            @RequestParam String sanction,
            RedirectAttributes redirectAttributes) {
        try {
            managementService.resolveDisciplinary(recordId, resolution, sanction);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Incidente disciplinario resuelto y archivado");

            var profileOpt = teacherProfileService.getProfileById(profileId);
            if (profileOpt.isEmpty() || profileOpt.get().getStaff() == null) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, PROFILE_NOT_FOUND_MSG);
                return REDIRECT_TEACHERS;
            }
            return "redirect:/academic/teachers/" + profileOpt.get().getStaff().getId() + "/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al resolver incidente: " + e.getMessage());
            return REDIRECT_TEACHERS;
        }
    }

    @PostMapping("/{staffId}/profile")
    public String saveProfile(@PathVariable @NonNull Long staffId,
            @Valid @ModelAttribute("profile") TeacherProfile profile,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Staff> staffOpt = staffService.getStaffById(staffId);
        if (staffOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, PROFILE_NOT_FOUND_MSG);
            return REDIRECT_TEACHERS;
        }
        Staff staff = staffOpt.get();
        profile.setStaff(staff);

        if (result.hasErrors()) {
            model.addAttribute("staff", staff);
            return "academic/teacher-profile";
        }

        try {
            teacherProfileService.saveProfile(profile);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Perfil docente actualizado exitosamente");
        } catch (Exception e) {
            model.addAttribute("staff", staff);
            model.addAttribute("errorMessage", "Error al guardar el perfil: " + e.getMessage());
            return "academic/teacher-profile";
        }

        return REDIRECT_TEACHERS;
    }
}
