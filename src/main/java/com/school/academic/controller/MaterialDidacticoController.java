package com.school.academic.controller;

import com.school.academic.entity.MaterialDidactico;
import com.school.academic.service.MaterialDidacticoService;
// ...existing code...
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/academic/material-didactico")
public class MaterialDidacticoController {
    private final MaterialDidacticoService materialDidacticoService;

    public MaterialDidacticoController(MaterialDidacticoService materialDidacticoService) {
        this.materialDidacticoService = materialDidacticoService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("materiales", materialDidacticoService.findAll());
        return "academic/material-didactico-list";
    }

    @GetMapping("/nuevo")
    public String form(Model model) {
        model.addAttribute("materialDidactico", new MaterialDidactico());
        return "academic/material-didactico-form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute MaterialDidactico materialDidactico, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("materialDidactico", materialDidactico);
            return "academic/material-didactico-form";
        }
        materialDidacticoService
                .save(java.util.Objects.requireNonNull(materialDidactico, "El material didáctico no puede ser null"));
        return "redirect:/academic/material-didactico";
    }

    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable Long id) {
        materialDidacticoService.delete(java.util.Objects.requireNonNull(id, "El ID no puede ser null"));
        return "redirect:/academic/material-didactico";
    }
}
