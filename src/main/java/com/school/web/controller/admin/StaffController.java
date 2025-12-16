package com.school.web.controller.admin;

import com.school.admin.entity.Staff;
import com.school.admin.service.StaffService;
import com.school.core.enums.Role;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private static final String STAFF_FORM_VIEW = "admin/staff-form";
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String listStaff(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("staffList", staffService.getAllStaff(pageable));
        return "admin/staff-list";
    }

    @GetMapping("/new")
    public String newStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("roles", Role.values());
        return STAFF_FORM_VIEW;
    }

    @PostMapping
    public String saveStaff(@jakarta.validation.Valid @ModelAttribute @NonNull Staff staff,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return STAFF_FORM_VIEW;
        }
        staffService.saveStaff(staff);
        return "redirect:/staff";
    }

    @GetMapping("/edit/{id}")
    public String editStaffForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("staff", staffService.getStaffById(id).orElseThrow());
        model.addAttribute("roles", Role.values());
        return STAFF_FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteStaff(@PathVariable @NonNull Long id) {
        staffService.deleteStaff(id);
        return "redirect:/staff";
    }
}
