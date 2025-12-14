package com.school.web.controller.academic;

import com.school.academic.entity.Section;
import com.school.academic.repository.SectionRepository;
import com.school.academic.repository.CourseRepository;
import com.school.admin.service.StaffService;
import com.school.infra.service.InfraService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sections")
public class SectionController {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final StaffService staffService;
    private final InfraService infraService;

    public SectionController(SectionRepository sectionRepository, CourseRepository courseRepository,
            StaffService staffService, InfraService infraService) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.staffService = staffService;
        this.infraService = infraService;
    }

    @GetMapping
    public String listSections(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("sections", sectionRepository.findAll(pageable));
        return "academic/section-list";
    }

    @GetMapping("/new")
    public String newSectionForm(Model model) {
        model.addAttribute("section", new Section());
        populateDropdowns(model);
        return "academic/section-form";
    }

    @PostMapping("/save")
    public String saveSection(@jakarta.validation.Valid @ModelAttribute @NonNull Section section,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateDropdowns(model);
            return "academic/section-form";
        }
        sectionRepository.save(section);
        return "redirect:/sections";
    }

    @GetMapping("/edit/{id}")
    public String editSectionForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("section", sectionRepository.findById(id).orElseThrow());
        populateDropdowns(model);
        return "academic/section-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSection(@PathVariable @NonNull Long id) {
        sectionRepository.deleteById(id);
        return "redirect:/sections";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", staffService.getAllTeachers());
        model.addAttribute("rooms", infraService.getAllRooms());
    }
}
