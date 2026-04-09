# Java Controller Generator

Genera controllers Thymeleaf para el School Management System.

## Template Controller
```java
package com.school.web.controller.${module};

import com.school.${module}.entity.${Entity};
import com.school.${module}.service.${Entity}Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/${resource}")
@PreAuthorize("hasAnyRole(${roles})")
@RequiredArgsConstructor
public class ${Entity}Controller {

    private final ${Entity}Service ${entity}Service;

    @GetMapping
    public String list(Model model, @PageableDefault(size = 25) Pageable pageable) {
        model.addAttribute("${resource}", ${entity}Service.findAll(pageable));
        return "${module}/${resource}/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("${entity}", new ${Entity}());
        return "${module}/${resource}/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("${entity}") ${Entity} entity,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) return "${module}/${resource}/form";
        ${entity}Service.create(entity);
        redirect.addFlashAttribute("success", "${entity}.created");
        return "redirect:/${resource}";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("${entity}", ${entity}Service.findById(id));
        return "${module}/${resource}/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("${entity}", ${entity}Service.findById(id));
        return "${module}/${resource}/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("${entity}") ${Entity} entity,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) return "${module}/${resource}/form";
        ${entity}Service.update(id, entity);
        redirect.addFlashAttribute("success", "${entity}.updated");
        return "redirect:/${resource}";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        ${entity}Service.softDelete(id);
        redirect.addFlashAttribute("success", "${entity}.deleted");
        return "redirect:/${resource}";
    }
}
```
