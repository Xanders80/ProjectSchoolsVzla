/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2025 [Tu Nombre o Empresa]
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.school.web.controller.academic;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.academic.entity.Course;
import com.school.academic.service.CourseService;
import com.school.academic.validation.ValidationGroups;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private static final String COURSE_FORM_VIEW = "academic/course-form";
    private final CourseService courseService;
    private final com.school.academic.service.CourseResourceService courseResourceService;

    public CourseController(CourseService courseService,
            com.school.academic.service.CourseResourceService courseResourceService) {
        this.courseService = courseService;
        this.courseResourceService = courseResourceService;
    }

    @GetMapping
    public String listCourses(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("courses", courseService.getAllActiveCourses(pageable));
        return "academic/course-list";
    }

    @GetMapping("/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return COURSE_FORM_VIEW;
    }

    @PostMapping
    public String saveCourse(
            @Validated({ ValidationGroups.Create.class,
                    ValidationGroups.Update.class }) @ModelAttribute @NonNull Course course,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            return COURSE_FORM_VIEW;
        }

        try {
            courseService.saveCourse(course);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            result.rejectValue("code", "error.course", "Ese código de curso ya está en uso");
            return COURSE_FORM_VIEW;
        }
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable @NonNull Long id, Model model) {
        Course course = courseService.getCourseById(id).orElseThrow();
        model.addAttribute("course", course);

        // Resources
        model.addAttribute("resources", courseResourceService.getResourcesByCourseId(id));
        model.addAttribute("newResource", new com.school.academic.entity.CourseResource());

        return COURSE_FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteCourse(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Curso eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Curso no encontrado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del sistema");
        }
        return "redirect:/courses";
    }

    @PostMapping("/{id}/resources")
    public String addResource(@PathVariable Long id,
            @ModelAttribute com.school.academic.entity.CourseResource newResource,
            RedirectAttributes redirectAttributes) {
        try {
            Course course = courseService.getCourseById(id).orElseThrow();

            // Simple logic: if URL is empty but we wanted a file, we'd handle MultipartFile
            // here.
            // For now assuming URL/Text entry.
            courseResourceService.createResource(course, newResource.getTitle(), newResource.getUrl(),
                    newResource.getResourceType());

            redirectAttributes.addFlashAttribute("successMessage", "Recurso agregado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al agregar recurso: " + e.getMessage());
        }
        return "redirect:/courses/edit/" + id;
    }

    @PostMapping("/resources/delete/{resourceId}")
    public String deleteResource(@PathVariable Long resourceId, @RequestParam Long courseId,
            RedirectAttributes redirectAttributes) {
        try {
            courseResourceService.deleteResource(resourceId);
            redirectAttributes.addFlashAttribute("successMessage", "Recurso eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar recurso: " + e.getMessage());
        }
        return "redirect:/courses/edit/" + courseId;
    }
}
