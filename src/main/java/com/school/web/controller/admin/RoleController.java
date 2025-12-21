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

package com.school.web.controller.admin;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.core.entity.Role;
import com.school.core.repository.RoleRepository;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private static final String SUCCESS_MESSAGE = "successMessage";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_ROLES = "redirect:/admin/roles";

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listRoles(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolePage = roleRepository.findAll(pageable);

        model.addAttribute("roles", rolePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rolePage.getTotalPages());
        model.addAttribute("totalItems", rolePage.getTotalElements());

        return "admin/role-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("isEdit", false);
        return "admin/role-form";
    }

    @PostMapping
    public String createRole(@ModelAttribute @NonNull Role role, RedirectAttributes redirectAttributes) {
        try {
            roleRepository.save(role);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Rol creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al crear el rol: " + e.getMessage());
        }
        return REDIRECT_ROLES;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isPresent()) {
            model.addAttribute("role", roleOpt.get());
            model.addAttribute("isEdit", true);
            return "admin/role-form";
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Rol no encontrado.");
            return REDIRECT_ROLES;
        }
    }

    @PostMapping("/edit/{id}")
    public String updateRole(@PathVariable Long id, @ModelAttribute Role role, RedirectAttributes redirectAttributes) {
        try {
            role.setId(id);
            roleRepository.save(role);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Rol actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al actualizar el rol: " + e.getMessage());
        }
        return REDIRECT_ROLES;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteRole(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            if (roleRepository.existsById(id)) {
                roleRepository.deleteById(id);
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Rol eliminado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Rol no encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al eliminar el rol: " + e.getMessage());
        }
        return REDIRECT_ROLES;
    }
}